import { LoginResponseResult, Jwt } from "./definitions";

// Helper function to get stored tokens
const getStoredTokens = (): { accessToken: string; refreshToken: string } | null => {
  // Safety check for server-side rendering
  if (typeof window === 'undefined') {
    console.log('Accessing localStorage in server context - this is expected during SSR');
    return null;
  }
  
  try {
    const accessToken = localStorage.getItem('access_token');
    const refreshToken = localStorage.getItem('refresh_token');
    
    if (!accessToken || !refreshToken) return null;
    
    return { accessToken, refreshToken };
  } catch (error) {
    console.error('Error accessing tokens from localStorage:', error);
    return null;
  }
};

// Check if access token is expired
const isTokenExpired = (token: string): boolean => {
  try {
    // Verify the token has the correct format
    if (!token || typeof token !== 'string' || !token.includes('.')) {
      console.error('Invalid token format');
      return true;
    }
    
    const parts = token.split('.');
    if (parts.length !== 3) {
      console.error('Token does not have three parts');
      return true;
    }
    
    // Decode the token
    let payload;
    try {
      // Handle potential padding issues with base64
      const base64Payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
      const padding = '==='.slice(0, (4 - base64Payload.length % 4) % 4);
      const jsonPayload = atob(base64Payload + padding);
      payload = JSON.parse(jsonPayload);
    } catch (decodeError) {
      console.error('Failed to decode token payload:', decodeError);
      return true;
    }
    
    // Check for expiration
    if (!payload.exp) {
      console.error('Token has no expiration claim');
      return true;
    }
    
    const expiry = payload.exp * 1000; // Convert to milliseconds
    const now = Date.now();
    const timeRemaining = expiry - now;
    
    console.log(`Token expiration check: expires in ${Math.floor(timeRemaining / 1000)} seconds`);
    
    // Add a buffer of 30 seconds to avoid edge cases
    return timeRemaining <= 30000;
  } catch (error) {
    console.error('Error checking token expiration:', error);
    return true;
  }
};

// Refresh token function
const refreshAccessToken = async (): Promise<string> => {
  console.log('Attempting to refresh access token');
  
  const tokens = getStoredTokens();
  if (!tokens) {
    console.error('Cannot refresh token: No refresh token available in storage');
    throw new Error('No refresh token available');
  }
  
  const clientId = process.env.NEXT_PUBLIC_CLIENT_ID || '';
  const clientSecret = process.env.NEXT_PUBLIC_CLIENT_SECRET || '';
  const authUrl = process.env.NEXT_PUBLIC_KEYCLOAK_URL || '';
  const realm = process.env.NEXT_PUBLIC_REALM || '';
  
  // Validate required environment variables
  if (!clientId || !clientSecret || !authUrl || !realm) {
    console.error('Missing environment variables for token refresh', {
      clientIdExists: !!clientId,
      clientSecretExists: !!clientSecret,
      authUrlExists: !!authUrl,
      realmExists: !!realm
    });
    throw new Error('Missing environment variables for token refresh');
  }
  
  console.log(`Refreshing token with Keycloak: ${authUrl}/realms/${realm}/protocol/openid-connect/token`);
  
  const formData = new URLSearchParams({
    client_id: clientId,
    grant_type: 'refresh_token',
    refresh_token: tokens.refreshToken,
    client_secret: clientSecret,
  });

  try {
    const response = await fetch(`${authUrl}/realms/${realm}/protocol/openid-connect/token`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      body: formData
    });

    console.log(`Token refresh response status: ${response.status}`);
    
    if (!response.ok) {
      const errorData = await response.text();
      console.error('Token refresh failed:', {
        status: response.status,
        statusText: response.statusText,
        errorData: errorData
      });
      throw new Error(`Failed to refresh token: ${response.status} ${response.statusText}`);
    }

    const data: Jwt = await response.json();
    
    if (!data.access_token || !data.refresh_token) {
      console.error('Token refresh response missing tokens:', data);
      throw new Error('Invalid token response from server');
    }
    
    console.log('Token refresh successful, storing new tokens');
    
    // Update stored tokens
    localStorage.setItem('access_token', data.access_token);
    localStorage.setItem('refresh_token', data.refresh_token);
    
    return data.access_token;
  } catch (error) {
    console.error('Error during token refresh:', error);
    throw error;
  }
};

// Get a valid access token (refreshing if needed)
const getValidAccessToken = async (): Promise<string> => {
  // Safety check for server-side rendering
  if (typeof window === 'undefined') {
    throw new Error('Cannot access tokens in server context');
  }
  
  const tokens = getStoredTokens();
  if (!tokens) throw new Error('No authentication tokens found');
  
  // Check if token is expired and refresh if needed
  if (isTokenExpired(tokens.accessToken)) {
    return await refreshAccessToken();
  }
  
  return tokens.accessToken;
};

// Authenticated fetch
const authFetch = async (url: string, options: RequestInit = {}): Promise<Response> => {
  try {
    console.log(`Attempting authenticated fetch to ${url}`);
    
    // Get a valid token
    let token;
    try {
      token = await getValidAccessToken();
      console.log('Successfully retrieved access token');
    } catch (error) {
      console.error('Failed to get valid access token:', error);
      throw new Error('Authentication failed: Unable to retrieve a valid token');
    }
    
    // Include the token in the headers
    const authOptions: RequestInit = {
      ...options,
      headers: {
        ...options.headers,
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      },
      // Ensure credentials are included (for cookies if your API uses them)
      credentials: 'include'
    };
    
    console.log('Making authenticated request with options:', {
      method: authOptions.method || 'GET',
      headers: authOptions.headers,
      url
    });
    
    // Make the request
    let response;
    try {
      response = await fetch(url, authOptions);
      console.log(`Response received: status ${response.status}`);
    } catch (fetchError) {
      console.error('Fetch operation failed:', fetchError);
      
      // Provide more detailed error information
      if (fetchError instanceof TypeError && fetchError.message === 'Failed to fetch') {
        throw new Error(
          `Network error: Could not connect to ${url}. ` +
          'This might be due to CORS restrictions, network connectivity, ' +
          'or the server being unavailable.'
        );
      }
      throw fetchError;
    }
    
    if (response.status === 401) {
      console.log('Received 401 Unauthorized, attempting token refresh');
      
      try {
        // If unauthorized, try refreshing token once
        const newToken = await refreshAccessToken();
        console.log('Token refresh successful, retrying request');
        
        // Retry the request with the new token
        return fetch(url, {
          ...authOptions,
          headers: {
            ...authOptions.headers,
            'Authorization': `Bearer ${newToken}`
          }
        });
      } catch (refreshError) {
        console.error('Token refresh failed:', refreshError);
        throw new Error('Authentication failed: Unable to refresh token');
      }
    }
    
    return response;
  } catch (error) {
    console.error('Auth fetch error:', error);
    throw error;
  }
};

export async function fetchActivePatients() {
  // If running on server-side, return empty array
  if (typeof window === 'undefined') {
    console.log('Running on server-side, returning empty patients array');
    return [];
  }
  
  // Check if user is logged in (has tokens)
  const tokens = getStoredTokens();
  if (!tokens) {
    console.error('No authentication tokens found');
    throw new Error('Authentication required: Please log in first');
  }
  
  try {
    console.log('Fetching patients from API');
    
    const API_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080';
    const endpoint = `${API_URL}/api/patients`;
    
    const response = await authFetch(endpoint);
    
    if (!response.ok) {
      const errorText = await response.text();
      console.error('Patient API error response:', {
        status: response.status,
        statusText: response.statusText,
        body: errorText
      });
      
      throw new Error(`HTTP error: ${response.status} ${response.statusText}`);
    }
    
    const patients = await response.json();
    console.log(`Successfully fetched ${patients.length} patients`);
    return patients;
  } catch (err) {
    console.error('Failed to fetch patients:', err);
    
    // Provide more helpful error messages based on error type
    if (err instanceof TypeError && err.message.includes('Failed to fetch')) {
      throw new Error(
        'Network error: Could not connect to the API server. ' +
        'Please check your network connection and ensure the server is running.'
      );
    }
    
    throw new Error(`Failed to fetch patient data: ${err instanceof Error ? err.message : String(err)}`);
  }
}

export async function fetchActiveNutritionists() {
  // If running on server-side, return empty array
  if (typeof window === 'undefined') {
    console.log('Running on server-side, returning empty nutritionists array');
    return [];
  }
  
  try {
    const response = await authFetch("http://localhost:8080/api/nutritionists");
    
    if (!response.ok) {
      throw new Error(`HTTP error: ${response.status}`);
    }
    
    const nutritionists = await response.json();
    return nutritionists;
  } catch (err) {
    console.error('API error: ', err);
    throw new Error('Failed to fetch nutritionist data');
  }
}

export async function login(clientId: string,
  username: string,
  password: string,
  clientSecret: string,
  authUrl: string,
  realm: string
) {
  try {
    const formData = new URLSearchParams({
      client_id: clientId,
      grant_type: "password",
      username: username,
      password: password,
      client_secret: clientSecret,
    });

    const response = await fetch(`${authUrl}/realms/${realm}/protocol/openid-connect/token`, {
      method: 'POST',
      body: formData
    });

    const loginResponseResult: LoginResponseResult = {
      jwt: await response.json(),
      ok: response.ok
    };

    loginResponseResult.ok = response.ok;
    return loginResponseResult;
  } catch(err) {
    console.error('Login error: ', err);
    throw new Error('Failed to login');
  }
}