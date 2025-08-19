import { fetcher } from '../fetcher';
import { InviteCodeAPIResponse } from '../types/organizations';

export const createInviteCode = (organizationId: number) => {
  return fetcher.post<InviteCodeAPIResponse>(`organizations/${organizationId}/invite-codes`);
};
