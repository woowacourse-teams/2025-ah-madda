import { getAuthCodeFromUrl, isAuthenticated, logout as logoutUser } from '../../api/auth';
import { useGoogleLoginMutation } from '../../api/authQueries';
import { ACCESS_TOKEN_KEY, OAUTH_PARAMS_TO_REMOVE } from '../constants';
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

      OAUTH_PARAMS_TO_REMOVE.forEach((param) => url.searchParams.delete(param));

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
