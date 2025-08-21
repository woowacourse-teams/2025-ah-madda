import { useMutation } from '@tanstack/react-query';

import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { setLocalStorage } from '@/shared/utils/localStorage';

import { fetcher } from '../fetcher';

type RefreshTokenResponse = {
  accessToken: string;
};

export const postRefreshToken = async (): Promise<RefreshTokenResponse> => {
  return await fetcher.post<RefreshTokenResponse>('members/token');
};

export const useRefreshToken = () => {
  return useMutation({
    mutationFn: postRefreshToken,
    onSuccess: (data) => {
      if (data?.accessToken) {
        setLocalStorage(ACCESS_TOKEN_KEY, data.accessToken);
      }
    },
    onError: (error) => {
      console.error('Token refresh failed:', error);
    },
  });
};
