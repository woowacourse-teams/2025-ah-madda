import { getAuthCodeFromUrl, isAuthenticated, logout as logoutUser } from '../../api/auth';
import { useGoogleLoginMutation } from '../../api/authQueries';
import { ACCESS_TOKEN_KEY } from '../constants';
import { setLocalStorage } from '../utils/localStorage';

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
    googleLoginMutation.mutate(getAuthCodeFromUrl() || '', {
      onSuccess: (data: { accessToken: string }) => {
        const token = data.accessToken;
        if (!token) {
          throw new Error('응답에서 액세스 토큰을 찾을 수 없습니다.');
        }
        setLocalStorage(ACCESS_TOKEN_KEY, token);
      },
      onError: (error) => {
        console.error('Google OAuth error:', error);
      },
    });
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
