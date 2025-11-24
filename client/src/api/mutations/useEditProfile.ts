import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { profileQueryKeys } from '../queries/profile';
import { ProfileAPIRequest } from '../types/profile';

export const editProfile = async (nickname: string, groupId: number) => {
  return await fetcher.patch(`open-profiles`, {
    nickname,
    groupId,
  });
};

export const useEditProfile = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: ({ nickname, groupId }: ProfileAPIRequest) => editProfile(nickname, groupId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [...profileQueryKeys.all()],
      });
    },
  });
};
