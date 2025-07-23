import { useMutation, UseMutationResult } from '@tanstack/react-query';

import { exchangeCodeForToken } from './auth';

export const authMutationKeys = {
  login: () => ['auth', 'login'],
};

export const useGoogleLoginMutation = (): UseMutationResult<
  { accessToken: string },
  Error,
  string
> => {
  return useMutation({
    mutationKey: authMutationKeys.login(),
    mutationFn: (code: string) => exchangeCodeForToken(code),
  });
};
