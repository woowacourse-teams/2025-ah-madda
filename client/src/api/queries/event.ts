import { queryOptions } from '@tanstack/react-query';

import { EventDetail } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export const eventQueryKeys = {
  all: () => ['event'],
  detail: () => [...eventQueryKeys.all(), 'detail'],
  alarm: () => [...eventQueryKeys.all(), 'alarm'],
};

export const eventQueryOptions = {
  detail: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.detail(), eventId],
      queryFn: () => getEventDetailAPI(eventId),
    }),
};

export const eventMutationOptions = {
  alarms: (eventId: number) => ({
    mutationKey: [...eventQueryKeys.alarm(), eventId],
    mutationFn: (content: string) => postAlarm(eventId, content),
  }),
};

const postAlarm = async (eventId: number, content: string) => {
  await fetcher.post(`events/${eventId}/notify-non-guests`, {
    json: {
      content,
    },
  });
};

const getEventDetailAPI = (eventId: number) => {
  return fetcher.get<EventDetail>(`organizations/events/${eventId}`);
};
