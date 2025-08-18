import { useMutation } from '@tanstack/react-query';

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
    { nickname }
  );
};

export const useCreateProfile = (organizationId: number) => {
  return useMutation({
    mutationFn: ({ nickname }: Pick<CreateOrganizationProfileRequest, 'nickname'>) =>
      createOrganizationProfile({ organizationId, nickname }),
  });
};
