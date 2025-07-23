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

const getHostEvents = async (organizationMemberId: number): Promise<Event[]> => {
  const response = await fetcher.get<Event[]>(
    `organization-members/${organizationMemberId}/events/owned`
  );
  return response;
};

const getParticipateEvents = async (organizationMemberId: number): Promise<Event[]> => {
  const response = await fetcher.get<Event[]>(
    `organization-members/${organizationMemberId}/events/participated`
  );
  return response;
};
