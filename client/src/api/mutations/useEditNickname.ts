import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { profileQueryKeys } from '../queries/profile';

export const editNickname = async (organizationId: number, nickname: string) => {
  return await fetcher.patch(`organizations/${organizationId}/organization-members/rename`, {
    nickname,
  });
};

export const useEditNickname = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ organizationId, nickname }: { organizationId: number; nickname: string }) =>
      editNickname(organizationId, nickname),
    onSuccess: (_, { organizationId }) => {
      // organizationProfile 쿼리 무효화
      queryClient.invalidateQueries({
        queryKey: [...profileQueryKeys.all(), organizationId],
      });
    },
  });
};
