import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { UpdateEventParams, UpdateEventResponse } from '../types/event';

export const useUpdateEvent = () => {
  return useMutation<UpdateEventResponse, Error, UpdateEventParams>({
    mutationFn: ({ eventId, payload }) => fetcher.patch(`organizations/events/${eventId}`, payload),
  });
};
