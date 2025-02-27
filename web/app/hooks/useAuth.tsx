// web/app/hooks/useAuth.ts
import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { 
  login as authLogin, 
  logout as authLogout,
  isLoggedIn, 
  getAccessToken,
  refreshToken,
  getRefreshToken 
} from '@/app/lib/auth';

export interface UseAuthReturn {
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (username: string, password: string) => Promise<boolean>;
  logout: () => void;
  token: string | null;
}

/**
 * React hook for authentication
 */
export default function useAuth(): UseAuthReturn {
  const [isAuthenticated, setIsAuthenticated] = useState<boolean>(false);
  const [isLoading, setIsLoading] = useState<boolean>(true);
  const [token, setToken] = useState<string | null>(null);
  const router = useRouter();

  useEffect(() => {
    // Check authentication status on mount and when dependencies change
    const checkAuth = async () => {
      setIsLoading(true);
      
      if (isLoggedIn()) {
        setIsAuthenticated(true);
        setToken(getAccessToken());
      } else if (getRefreshToken()) {
        // Try to refresh the token
        try {
          const refreshed = await refreshToken(getRefreshToken()!);
          if (refreshed) {
            setIsAuthenticated(true);
            setToken(refreshed.access_token);
          } else {
            setIsAuthenticated(false);
            setToken(null);
          }
        } catch (error) {
          setIsAuthenticated(false);
          setToken(null);
        }
      } else {
        setIsAuthenticated(false);
        setToken(null);
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
        setIsLoading(false);
        return true;
      } else {
        setIsAuthenticated(false);
        setToken(null);
        setIsLoading(false);
        return false;
      }
    } catch (error) {
      console.error('Login error:', error);
      setIsAuthenticated(false);
      setToken(null);
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
    router.push('/login');
  };

  return {
    isAuthenticated,
    isLoading,
    login,
    logout,
    token
  };
}