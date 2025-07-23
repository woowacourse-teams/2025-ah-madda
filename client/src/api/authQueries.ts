import { useMutation, UseMutationResult } from '@tanstack/react-query';

import { accessToken, exchangeCodeForToken } from './auth';

export const authMutationKeys = {
  login: () => ['auth', 'login'],
};

export const useGoogleLoginMutation = (): UseMutationResult<accessToken, Error, string> => {
  return useMutation({
    mutationKey: authMutationKeys.login(),
    mutationFn: (code: string) => exchangeCodeForToken(code),
  });
};
