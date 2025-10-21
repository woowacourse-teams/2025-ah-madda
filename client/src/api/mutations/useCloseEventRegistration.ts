import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryKeys } from '../queries/event';

export const closeEventRegistration = async (eventId: number) => {
  return await fetcher.post(`organizations/events/${eventId}/registration/close`);
};

export const useCloseEventRegistration = (eventId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => closeEventRegistration(eventId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.detail(), eventId],
      });
    },
  });
};
