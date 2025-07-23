import {
  getAuthCodeFromUrl,
  saveAuthData,
  isAuthenticated,
  getStoredToken,
  logout as logoutUser,
} from '../api/auth';
import { useGoogleLoginMutation } from '../api/authQueries';

type UseGoogleAuthReturn = {
  isLoading: boolean;
  isAuthenticated: boolean;
  error: string | null;
  handleCallback: () => Promise<void>;
  logout: () => void;
};

export const useGoogleAuth = (): UseGoogleAuthReturn => {
  const googleLoginMutation = useGoogleLoginMutation();

  const handleCallback = async (): Promise<void> => {
    try {
      const existingToken = getStoredToken();
      if (existingToken) {
        return;
      }

      const code = getAuthCodeFromUrl();
      if (!code) {
        throw new Error('Authorization code not found in URL');
      }

      const response = await googleLoginMutation.mutateAsync(code);

      const token = response.accessToken;
      if (!token) {
        throw new Error('Access token not found in response');
      }

      saveAuthData(token);

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
    googleLoginMutation.reset();
  };

  return {
    isLoading: googleLoginMutation.isPending,
    isAuthenticated: isAuthenticated(),
    error: googleLoginMutation.error?.message || null,
    handleCallback,
    logout,
  };
};
