import { queryOptions } from '@tanstack/react-query';

import { CreateEventRequest, EventDetail } from '../../features/Event/types/Event';
import { fetcher } from '../fetcher';

type CreateEventAPIResponse = {
  eventId: number;
};

export const eventQueryKeys = {
  all: () => ['event'],
  detail: () => [...eventQueryKeys.all(), 'detail'],
};

export const eventQueryOptions = {
  detail: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.detail(), eventId],
      queryFn: () => getEventDetailAPI(eventId),
    }),
};

export const createEventAPI = (organizationId: number, data: CreateEventRequest) => {
  return fetcher.post<CreateEventAPIResponse>(`organizations/${organizationId}/events`, {
    json: data,
  });
};

const getEventDetailAPI = (eventId: number) => {
  return fetcher.get<EventDetail>(`organizations/events/${eventId}`);
};
