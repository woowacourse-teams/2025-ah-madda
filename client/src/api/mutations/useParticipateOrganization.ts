import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { organizationQueryKeys } from '../queries/organization';
import { OrganizationParticipateAPIRequest } from '../types/organizations';

const participateOrganization = async (
  organizationId: number,
  data: OrganizationParticipateAPIRequest
) => {
  return fetcher.post(`organizations/${organizationId}/participation`, data);
};

export const useParticipateOrganization = (organizationId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (data: OrganizationParticipateAPIRequest) =>
      participateOrganization(organizationId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [...organizationQueryKeys.joinedStatus(), organizationId],
      });

      queryClient.invalidateQueries({
        queryKey: organizationQueryKeys.joined(),
      });
    },
  });
};
