import { useMutation, useQueryClient } from '@tanstack/react-query';

import { CreateEventAPIRequest } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

type UpdateEventParams = {
  eventId: number;
  payload: CreateEventAPIRequest;
};

type UpdateEventResponse = {
  eventId: number;
};

export const useUpdateEvent = () => {
  const queryClient = useQueryClient();

  return useMutation<UpdateEventResponse, Error, UpdateEventParams>({
    mutationFn: ({ eventId, payload }) => fetcher.patch(`organizations/events/${eventId}`, payload),
    onSuccess: () => {
      queryClient.invalidateQueries();
    },
  });
};
