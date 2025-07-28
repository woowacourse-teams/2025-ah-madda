import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';

export const createOrganizationProfile = ({
  organizationId,
  nickname,
}: {
  organizationId: number;
  nickname: string;
}) => {
  return fetcher.post<{ nickname: string }>(`organizations/${organizationId}/participation`, {
    json: { nickname },
  });
};

export const useCreateProfile = (organizationId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (nickname: string) => createOrganizationProfile({ organizationId, nickname }),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: ['organization', 'profile', organizationId],
      });
      queryClient.invalidateQueries({
        queryKey: ['organization', 'event', organizationId],
      });
    },
  });
};
