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
      const keys = [
        [...eventQueryKeys.detail(), eventId],
        [...eventQueryKeys.guests(), eventId],
        [...eventQueryKeys.nonGuests(), eventId],
        [...eventQueryKeys.guestStatus(), eventId],
      ];

      keys.forEach((key) => {
        queryClient.invalidateQueries({
          queryKey: key,
        });
      });
    },
  });
};
