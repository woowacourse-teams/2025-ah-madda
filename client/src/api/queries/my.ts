import { Event } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export const myQueryKeys = {
  all: () => ['my'],
  profile: () => [...myQueryKeys.all(), 'profile'],
  event: {
    all: () => [...myQueryKeys.all(), 'event'],
    host: () => [...myQueryKeys.event.all(), 'host'],
    participate: () => [...myQueryKeys.event.all(), 'participate'],
  },
};

export const myQueryOptions = {
  profile: () => ({
    queryKey: myQueryKeys.profile(),
    queryFn: getMyProfile,
  }),
  event: {
    hostEvents: (organizationId: number) => ({
      queryKey: [...myQueryKeys.event.host(), organizationId],
      queryFn: () => getHostEvents(organizationId),
    }),
    participateEvents: (organizationId: number) => ({
      queryKey: [...myQueryKeys.event.participate(), organizationId],
      queryFn: () => getParticipateEvents(organizationId),
    }),
  },
};

const getMyProfile = async (): Promise<{ id: number; name: string; email: string }> => {
  return await fetcher.get('members/profile');
};

const getHostEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/owned`);
};

const getParticipateEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/participated`);
};
