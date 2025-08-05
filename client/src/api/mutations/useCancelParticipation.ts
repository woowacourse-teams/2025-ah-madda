import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryKeys } from '../queries/event';

export const deleteParticipation = async (eventId: number) => {
  await fetcher.delete(`events/${eventId}/cancel-participate`);
};

export const useCancelParticipation = (eventId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: () => deleteParticipation(eventId),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.guestStatus(), eventId],
      });
    },
  });
};
