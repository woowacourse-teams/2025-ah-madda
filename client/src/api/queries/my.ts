import { Event } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export const myEventQueryKeys = {
  all: () => ['myEvent'],
  host: () => [...myEventQueryKeys.all(), 'host'],
  participate: () => [...myEventQueryKeys.all(), 'participate'],
};

export const myEventQueryOptions = {
  hostEvents: (organizationMemberId: number) => ({
    queryKey: [...myEventQueryKeys.host(), organizationMemberId],
    queryFn: () => getHostEvents(organizationMemberId),
  }),
  participateEvents: (organizationMemberId: number) => ({
    queryKey: [...myEventQueryKeys.participate(), organizationMemberId],
    queryFn: () => getParticipateEvents(organizationMemberId),
  }),
};

const getHostEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/owned`);
};

const getParticipateEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/participated`);
};
