import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';

type CreateOrganizationProfileRequest = {
  organizationId: number;
  nickname: string;
};

export const createOrganizationProfile = ({
  organizationId,
  nickname,
}: CreateOrganizationProfileRequest) => {
  return fetcher.post<Pick<CreateOrganizationProfileRequest, 'nickname'>>(
    `organizations/${organizationId}/participation`,
    {
      json: { nickname },
    }
  );
};

export const useCreateProfile = (organizationId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ nickname }: Pick<CreateOrganizationProfileRequest, 'nickname'>) =>
      createOrganizationProfile({ organizationId, nickname }),
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
