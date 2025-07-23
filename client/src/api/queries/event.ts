import { queryOptions } from '@tanstack/react-query';

import { CreateEventRequest, EventDetail } from '../../features/Event/types/Event';
import { fetcher } from '../fetcher';

export const eventQueryKeys = {
  all: () => ['event'],
  detail: (eventId: number) => [...eventQueryKeys.all(), 'detail', eventId],
};

export const eventQueryOptions = {
  detail: (eventId: number) =>
    queryOptions({
      queryKey: eventQueryKeys.detail(eventId),
      queryFn: () => getEventDetailAPI(eventId),
    }),
};

export const createEventAPI = (organizationId: number, data: CreateEventRequest) => {
  return fetcher.post<{ eventId: number }>(`organizations/${organizationId}/events`, {
    json: data,
  });
};

const getEventDetailAPI = (eventId: number) => {
  return fetcher.get<EventDetail>(`organizations/events/${eventId}`);
};
