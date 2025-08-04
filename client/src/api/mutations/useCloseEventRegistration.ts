import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryKeys } from '../queries/event';

export const closeEventRegistration = async (eventId: number) => {
  await fetcher.post(`organizations/events/${eventId}/registration/close`);
};

export const useCloseEventRegistration = (eventId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => closeEventRegistration(eventId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.detail(), eventId],
      });
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.guests(), eventId],
      });
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.nonGuests(), eventId],
      });
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.guestStatus(), eventId],
      });
    },
  });
};
