import { useMutation, UseMutationResult } from '@tanstack/react-query';

import { ACCESS_TOKEN_KEY } from '../shared/constants';
import { setLocalStorage } from '../shared/utils/localStorage';

import { accessToken, exchangeCodeForToken } from './auth';

export const useGoogleLoginMutation = (): UseMutationResult<accessToken, Error, string> => {
  return useMutation({
    mutationFn: (code: string) => exchangeCodeForToken(code),
    onSuccess: (data) => {
      setLocalStorage(ACCESS_TOKEN_KEY, data.accessToken);
    },
  });
};
