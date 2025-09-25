import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { profileQueryKeys } from '../queries/profile';

export const editProfile = async (organizationId: number, nickname: string, groupId: number) => {
  return await fetcher.patch(`organizations/${organizationId}/organization-members`, {
    nickname,
    groupId,
  });
};

export const useEditProfile = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({
      organizationId,
      nickname,
      groupId,
    }: {
      organizationId: number;
      nickname: string;
      groupId: number;
    }) => editProfile(organizationId, nickname, groupId),
    onSuccess: (_, { organizationId }) => {
      queryClient.invalidateQueries({
        queryKey: [...profileQueryKeys.all(), organizationId],
      });
    },
  });
};
