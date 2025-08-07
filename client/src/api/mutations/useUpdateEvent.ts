import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { UpdateEventParams, UpdateEventResponse } from '../types/event';

export const useUpdateEvent = () => {
  const queryClient = useQueryClient();

  return useMutation<UpdateEventResponse, Error, UpdateEventParams>({
    mutationFn: ({ eventId, payload }) => fetcher.patch(`organizations/events/${eventId}`, payload),
    onSuccess: () => {
      queryClient.invalidateQueries();
    },
  });
};
