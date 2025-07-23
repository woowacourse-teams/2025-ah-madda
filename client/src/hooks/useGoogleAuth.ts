import { useState } from 'react';

import {
  getAuthCodeFromUrl,
  exchangeCodeForToken,
  saveAuthData,
  isAuthenticated,
  logout as logoutUser,
} from '../api/auth';

type UseGoogleAuthReturn = {
  isLoading: boolean;
  isAuthenticated: boolean;
  error: string | null;
  handleCallback: () => Promise<void>;
  logout: () => void;
};

export const useGoogleAuth = (): UseGoogleAuthReturn => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleCallback = async (): Promise<void> => {
    setIsLoading(true);
    setError(null);

    try {
      const code = getAuthCodeFromUrl();

      if (!code) {
        throw new Error('Authorization code not found in URL');
      }

      const response = await exchangeCodeForToken(code);

      const token = response.accessToken;
      if (!token) {
        throw new Error('Access token not found in response');
      }

      saveAuthData(token);

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
    setError(null);
  };

  return {
    isLoading,
    isAuthenticated: isAuthenticated(),
    error,
    handleCallback,
    logout,
  };
};
