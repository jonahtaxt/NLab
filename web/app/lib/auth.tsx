import { Jwt, LoginResponseResult } from "./definitions";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api';

export async function login(
  username: string,
  password: string,
  clientId = process.env.NEXT_PUBLIC_CLIENT_ID || '',
  clientSecret = process.env.NEXT_PUBLIC_CLIENT_SECRET || '',
  authUrl = process.env.NEXT_PUBLIC_KEYCLOAK_URL || '',
  realm = process.env.NEXT_PUBLIC_REALM || ''
): Promise<LoginResponseResult> {
  try {
    const formData = new URLSearchParams({
      client_id: clientId,
      grant_type: 'password',
      username: username,
      password: password,
      client_secret: clientSecret,
    });

    const response = await fetch(`${authUrl}/realms/${realm}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData
    });

    const jwt = await response.json();
    
    // Store tokens if login successful
    if (response.ok) {
      storeTokens(jwt);
    }

    return {
      jwt,
      ok: response.ok
    };
  } catch (err) {
    console.error('Login error:', err);
    throw new Error('Failed to login');
  }
}

export async function refreshToken(
  refreshToken: string,
  clientId = process.env.NEXT_PUBLIC_CLIENT_ID || '',
  clientSecret = process.env.NEXT_PUBLIC_CLIENT_SECRET || '',
  authUrl = process.env.NEXT_PUBLIC_KEYCLOAK_URL || '',
  realm = process.env.NEXT_PUBLIC_REALM || ''
): Promise<Jwt | null> {
  try {
    const formData = new URLSearchParams({
      client_id: clientId,
      grant_type: 'refresh_token',
      refresh_token: refreshToken,
      client_secret: clientSecret,
    });

    const response = await fetch(`${authUrl}/realms/${realm}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData
    });

    if (!response.ok) {
      removeTokens();
      return null;
    }

    const tokens = await response.json();
    storeTokens(tokens);
    return tokens;
  } catch (err) {
    console.error('Token refresh error:', err);
    removeTokens();
    return null;
  }
}

export function storeTokens(jwt: Jwt): void {
  if (typeof window !== 'undefined') {
    localStorage.setItem('access_token', jwt.access_token);
    localStorage.setItem('refresh_token', jwt.refresh_token);
    localStorage.setItem('token_expiry', (Date.now() + jwt.expires_in * 1000).toString());
  }
}

export function removeTokens(): void {
  if (typeof window !== 'undefined') {
    localStorage.removeItem('access_token');
    localStorage.removeItem('refresh_token');
    localStorage.removeItem('token_expiry');
  }
}

export function isTokenExpired(): boolean {
  if (typeof window !== 'undefined') {
    const expiry = localStorage.getItem('token_expiry');
    if (!expiry) return true;
    
    return Date.now() > parseInt(expiry);
  }
  return true;
}

export function getAccessToken(): string | null {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('access_token');
  }
  return null;
}

export function getRefreshToken(): string | null {
  if (typeof window !== 'undefined') {
    return localStorage.getItem('refresh_token');
  }
  return null;
}

export function isLoggedIn(): boolean {
  const token = getAccessToken();
  return !!token && !isTokenExpired();
}

export function logout(): void {
  removeTokens();
}

export async function authFetch(
  endpoint: string,
  options: RequestInit = {}
): Promise<Response> {
  const url = endpoint.startsWith('http') ? endpoint : `${API_BASE_URL}${endpoint}`;
  
  let token = getAccessToken();
  
  if (isTokenExpired() && getRefreshToken()) {
    const refreshedTokens = await refreshToken(getRefreshToken()!);
    if (refreshedTokens) {
      token = refreshedTokens.access_token;
    } else {
      if (typeof window !== 'undefined') {
        removeTokens();
        window.location.href = '/login';
      }
      throw new Error('Authentication required');
    }
  }
  
  if (!token) {
    if (typeof window !== 'undefined') {
      window.location.href = '/login';
    }
    throw new Error('Authentication required');
  }
  
  const headers = {
    ...options.headers,
    Authorization: `Bearer ${token}`,
  };
  
  return fetch(url, {
    ...options,
    headers,
  });
}

export async function authGet<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
  const response = await authFetch(endpoint, {
    ...options,
    method: 'GET',
  });
  
  if (!response.ok) {
    throw new Error(`API error: ${response.status} ${response.statusText}`);
  }
  
  return await response.json();
}

export async function authPost<T>(endpoint: string, data: any, options: RequestInit = {}): Promise<T> {
  const response = await authFetch(endpoint, {
    ...options,
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    body: JSON.stringify(data),
  });
  
  if (!response.ok) {
    throw new Error(`API error: ${response.status} ${response.statusText}`);
  }
  
  return await response.json();
}

export async function authPut<T>(endpoint: string, data: any, options: RequestInit = {}): Promise<T> {
  const response = await authFetch(endpoint, {
    ...options,
    method: 'PUT',
    headers: {
      'Content-Type': 'application/json',
      ...options.headers,
    },
    body: JSON.stringify(data),
  });
  
  if (!response.ok) {
    throw new Error(`API error: ${response.status} ${response.statusText}`);
  }
  
  return await response.json();
}

export async function authDelete(endpoint: string, options: RequestInit = {}): Promise<void> {
  const response = await authFetch(endpoint, {
    ...options,
    method: 'DELETE',
  });
  
  if (!response.ok) {
    throw new Error(`API error: ${response.status} ${response.statusText}`);
  }
}

// Add these functions to web/app/lib/auth.tsx

/**
 * Decodes a JWT token and returns the payload
 * @param token JWT token string
 * @returns Decoded token payload or null if invalid
 */
export function decodeToken(token: string | null): any {
  if (!token) return null;
  
  try {
    // JWT tokens consist of three parts: header.payload.signature
    const parts = token.split('.');
    if (parts.length !== 3) {
      console.error('Invalid token format');
      return null;
    }
    
    // Get the payload part (second part)
    const base64Url = parts[1];
    
    // Convert base64url to base64
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    
    // Decode base64
    const rawPayload = atob(base64);
    
    // Parse JSON
    return JSON.parse(rawPayload);
  } catch (error) {
    console.error('Error decoding token:', error);
    return null;
  }
}

/**
 * Extracts user roles from the current access token
 * @returns Array of user role strings
 */
export function getUserRoles(): string[] {
  const token = getAccessToken();
  const decoded = decodeToken(token);
  
  if (!decoded || !decoded.realm_access || !decoded.realm_access.roles) {
    return [];
  }
  
  return decoded.realm_access.roles;
}

/**
 * Checks if the current user has a specific role
 * @param role Role to check
 * @returns Boolean indicating whether user has the role
 */
export function hasRole(role: string): boolean {
  const roles = getUserRoles();
  return roles.includes(role);
}