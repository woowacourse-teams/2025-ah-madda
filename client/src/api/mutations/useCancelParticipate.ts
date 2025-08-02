import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryKeys } from '../queries/event';

export const deleteParticipate = async (eventId: number) => {
  await fetcher.delete(`events/${eventId}/cancel-participate`);
};

export const useCancelParticipate = (eventId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => deleteParticipate(eventId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.guestStatus(), eventId],
      });
    },
  });
};
