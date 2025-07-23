import { useEffect } from 'react';

import { getAuthCodeFromUrl, isAuthenticated, logout as logoutUser } from '../../api/auth';
import { useGoogleLoginMutation } from '../../api/authQueries';
import { ACCESS_TOKEN_KEY } from '../constants';
import { getLocalStorage, setLocalStorage } from '../utils/localStorage';

type UseGoogleAuthReturn = {
  isLoading: boolean;
  isAuthenticated: boolean;
  error: string | null;
  handleCallback: () => Promise<void>;
  logout: () => void;
};

export const useGoogleAuth = (): UseGoogleAuthReturn => {
  const googleLoginMutation = useGoogleLoginMutation();

  useEffect(() => {
    const processAutoLogin = async () => {
      if (isAuthenticated()) {
        return;
      }

      const code = getAuthCodeFromUrl();
      if (!code) {
        return;
      }

      try {
        const existingToken = getLocalStorage(ACCESS_TOKEN_KEY);
        if (existingToken) {
          return;
        }

        const response = await googleLoginMutation.mutateAsync(code);

        const token = response.accessToken;
        if (!token) {
          throw new Error('Access token not found in response');
        }

        setLocalStorage(ACCESS_TOKEN_KEY, token);

        const url = new URL(window.location.href);
        url.searchParams.delete('code');
        url.searchParams.delete('state');
        url.searchParams.delete('scope');
        url.searchParams.delete('authuser');
        url.searchParams.delete('prompt');
        window.history.replaceState({}, document.title, url.toString());
      } catch (err) {
        console.error('Auto login failed:', err);
      }
    };

    processAutoLogin();
  }, []);

  const handleCallback = async (): Promise<void> => {
    try {
      const existingToken = getLocalStorage(ACCESS_TOKEN_KEY);
      if (existingToken) {
        return;
      }

      const code = getAuthCodeFromUrl();
      if (!code) {
        throw new Error('인가 코드를 url에서 찾을 수 없습니다.');
      }

      const response = await googleLoginMutation.mutateAsync(code);

      const token = response.accessToken;
      if (!token) {
        throw new Error('응답에서 액세스 토큰을 찾을 수 없습니다.');
      }

      setLocalStorage(ACCESS_TOKEN_KEY, token);

      const url = new URL(window.location.href);
      url.searchParams.delete('code');
      url.searchParams.delete('state');
      url.searchParams.delete('scope');
      url.searchParams.delete('authuser');
      url.searchParams.delete('prompt');
      window.history.replaceState({}, document.title, url.toString());
    } catch (err) {
      console.error('Google OAuth error:', err);
      throw err;
    }
  };

  const logout = (): void => {
    logoutUser();
  };

  return {
    isLoading: googleLoginMutation.isPending,
    isAuthenticated: isAuthenticated(),
    error: googleLoginMutation.error?.message || null,
    handleCallback,
    logout,
  };
};
