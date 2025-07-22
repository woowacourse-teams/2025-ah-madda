import { useState, useEffect } from 'react';

import {
  getAuthCodeFromUrl,
  exchangeCodeForToken,
  saveAuthData,
  getStoredUser,
  isAuthenticated,
  logout as logoutUser,
} from '../api/auth';

type User = {
  id: string;
  email: string;
  name: string;
  picture: string;
};

type UseGoogleAuthReturn = {
  user: User | null;
  isLoading: boolean;
  isAuthenticated: boolean;
  error: string | null;
  handleCallback: () => Promise<void>;
  logout: () => void;
};

export const useGoogleAuth = (): UseGoogleAuthReturn => {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const storedUser = getStoredUser();
    if (storedUser) {
      setUser(storedUser);
    }
  }, []);

  const handleCallback = async (): Promise<void> => {
    setIsLoading(true);
    setError(null);

    try {
      const code = getAuthCodeFromUrl();

      if (!code) {
        throw new Error('Authorization code not found in URL');
      }

      const response = await exchangeCodeForToken(code);

      saveAuthData(response.access_token, response.user);

      setUser(response.user);

      const url = new URL(window.location.href);
      url.searchParams.delete('code');
      url.searchParams.delete('state');
      window.history.replaceState({}, document.title, url.toString());
    } catch (err) {
      const errorMessage = err instanceof Error ? err.message : 'Login failed';
      setError(errorMessage);
      console.error('Google OAuth error:', err);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = (): void => {
    logoutUser();
    setUser(null);
    setError(null);
  };

  return {
    user,
    isLoading,
    isAuthenticated: isAuthenticated(),
    error,
    handleCallback,
    logout,
  };
};
