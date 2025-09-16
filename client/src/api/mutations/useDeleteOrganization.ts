import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '@/api/fetcher';

import { organizationQueryKeys } from '../queries/organization';

const deleteOrganization = (organizationId: number) => {
  return fetcher.delete(`organizations/${organizationId}`);
};

export const useDeleteOrganization = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (organizationId: number) => deleteOrganization(organizationId),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: [...organizationQueryKeys.all()] });
    },
  });
};
