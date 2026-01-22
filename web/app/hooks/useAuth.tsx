import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { 
  login as authLogin, 
  logout as authLogout,
  isLoggedIn, 
  getAccessToken,
  refreshToken,
  getRefreshToken,
  decodeToken
} from '@/app/lib/auth';

export interface UseAuthReturn {
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  token: string | null;
  userRoles: string[];
  hasRole: (role: string) => boolean;
}

/**
 * React hook for authentication with role-based access control
 */
export default function useAuth(): UseAuthReturn {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [token, setToken] = useState<string | null>(null);
  const [userRoles, setUserRoles] = useState<string[]>([]);
  const router = useRouter();

  // Function to extract roles from token
  const extractRolesFromToken = (accessToken: string | null): string[] => {
    if (!accessToken) return [];
    
    try {
      const decoded = decodeToken(accessToken);
      
      if (!decoded || !decoded.realm_access || !decoded.realm_access.roles) {
        return [];
      }
      
      return decoded.realm_access.roles.map((role: string) => role.toUpperCase());
    } catch (error) {
      console.error('Error extracting roles from token:', error);
      return [];
    }
  };

  useEffect(() => {
    // Check authentication status on mount and when dependencies change
    const checkAuth = async () => {
      setIsLoading(true);
      
      if (isLoggedIn()) {
        const accessToken = getAccessToken();
        setIsAuthenticated(true);
        setToken(accessToken);
        
        // Extract roles from token
        const roles = extractRolesFromToken(accessToken);
        setUserRoles(roles);
      } else if (getRefreshToken()) {
        // Try to refresh the token
        try {
          const refreshed = await refreshToken(getRefreshToken()!);
          if (refreshed) {
            setIsAuthenticated(true);
            setToken(refreshed.access_token);
            
            // Extract roles after refresh
            const roles = extractRolesFromToken(refreshed.access_token);
            setUserRoles(roles);
          } else {
            setIsAuthenticated(false);
            setToken(null);
            setUserRoles([]);
          }
        } catch (error) {
          setIsAuthenticated(false);
          setToken(null);
          setUserRoles([]);
        }
      } else {
        setIsAuthenticated(false);
        setToken(null);
        setUserRoles([]);
      }
      
      setIsLoading(false);
    };

    checkAuth();
  }, []);

  /**
   * Login function
   */
  const login = async (username: string, password: string): Promise<boolean> => {
    setIsLoading(true);
    
    try {
      const result = await authLogin(username, password);
      
      if (result.ok) {
        setIsAuthenticated(true);
        setToken(result.jwt.access_token);
        
        // Extract roles from the new token
        const roles = extractRolesFromToken(result.jwt.access_token);
        setUserRoles(roles);
        
        setIsLoading(false);
        return true;
      } else {
        setIsAuthenticated(false);
        setToken(null);
        setUserRoles([]);
        setIsLoading(false);
        return false;
      }
    } catch (error) {
      console.error('Login error:', error);
      setIsAuthenticated(false);
      setToken(null);
      setUserRoles([]);
      setIsLoading(false);
      return false;
    }
  };

  /**
   * Logout function
   */
  const logout = () => {
    authLogout();
    setIsAuthenticated(false);
    setToken(null);
    setUserRoles([]);
    router.push('/login');
  };

  /**
   * Check if user has a specific role
   */
  const hasRole = (role: string): boolean => {
    return userRoles.includes(role);
  };

  return {
    isAuthenticated,
    isLoading,
    login,
    logout,
    token,
    userRoles,
    hasRole
  };
}