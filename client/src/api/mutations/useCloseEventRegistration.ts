import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryKeys } from '../queries/event';

export const closeEventRegistration = async (eventId: number) => {
  return await fetcher.post(`organizations/events/${eventId}/registration/close`);
};

export const useCloseEventRegistration = () => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (eventId: number) => closeEventRegistration(eventId),
    onSuccess: (eventId) => {
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
