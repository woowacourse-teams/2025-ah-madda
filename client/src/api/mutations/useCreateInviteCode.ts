import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { InviteCodeAPIResponse } from '../types/organizations';

export const createInviteCode = (organizationId: number) => {
  return fetcher.post<InviteCodeAPIResponse>(`organizations/${organizationId}/invite-codes`);
};

export const useCreateInviteCode = (organizationId: number) => {
  return useMutation({
    mutationFn: () => createInviteCode(organizationId),
  });
};
