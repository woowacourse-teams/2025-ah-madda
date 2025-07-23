import { Event } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export const myEventQueryKeys = {
  hostEvents: () => ['myEvent', 'hostEvents'],
  participateEvents: () => ['myEvent', 'participateEvents'],
};

export const myEventQueryOptions = {
  hostEvents: (organizationMemberId: number) => ({
    queryKey: [...myEventQueryKeys.hostEvents(), organizationMemberId],
    queryFn: () => getHostEvents(organizationMemberId),
  }),
  participateEvents: (organizationMemberId: number) => ({
    queryKey: [...myEventQueryKeys.participateEvents(), organizationMemberId],
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
