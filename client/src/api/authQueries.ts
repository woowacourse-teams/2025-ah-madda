import { useMutation, UseMutationResult } from '@tanstack/react-query';

import { accessToken, exchangeCodeForToken } from './auth';

export const useGoogleLoginMutation = (): UseMutationResult<accessToken, Error, string> => {
  return useMutation({
    mutationFn: (code: string) => exchangeCodeForToken(code),
  });
};
