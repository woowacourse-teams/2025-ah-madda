import { queryOptions } from '@tanstack/react-query';

import { Event } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export const myQueryKeys = {
  all: () => ['my'],
  event: {
    all: () => [...myQueryKeys.all(), 'event'],
    host: () => [...myQueryKeys.event.all(), 'host'],
    participate: () => [...myQueryKeys.event.all(), 'participate'],
  },
};

export const myQueryOptions = {
  event: {
    hostEvents: (organizationId: number) =>
      queryOptions({
        queryKey: [...myQueryKeys.event.host(), organizationId],
        queryFn: () => getHostEvents(organizationId),
      }),

    participateEvents: (organizationId: number) =>
      queryOptions({
        queryKey: [...myQueryKeys.event.participate(), organizationId],
        queryFn: () => getParticipateEvents(organizationId),
      }),
  },
};

const getHostEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/owned`);
};

const getParticipateEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/participated`);
};
