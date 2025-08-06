import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { OrganizationParticipateAPIRequest } from '../types/organizations';

const participateOrganization = async (
  organizationId: number,
  data: OrganizationParticipateAPIRequest
) => {
  return fetcher.post(`organizations/${organizationId}/participation`, data);
};

export const useParticipateOrganization = (organizationId: number) => {
  return useMutation({
    mutationFn: (data: OrganizationParticipateAPIRequest) =>
      participateOrganization(organizationId, data),
  });
};
