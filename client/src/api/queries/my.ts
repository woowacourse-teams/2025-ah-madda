import { queryOptions } from '@tanstack/react-query';

import { Event } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';
import { GuestAnswerAPIResponse } from '../types/my';

export const myQueryKeys = {
  all: () => ['my'],
  event: {
    all: () => [...myQueryKeys.all(), 'event'],
    host: () => [...myQueryKeys.event.all(), 'host'],
    participate: () => [...myQueryKeys.event.all(), 'participate'],
    guestAnswers: () => [...myQueryKeys.event.all(), 'guestAnswers'],
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

    guestAnswers: (eventId: number, guestId: number) =>
      queryOptions({
        queryKey: [...myQueryKeys.event.guestAnswers(), eventId, guestId],
        queryFn: () => getGuestAnswers(eventId, guestId),
      }),
  },
};

const getHostEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/owned`);
};

const getParticipateEvents = async (organizationId: number): Promise<Event[]> => {
  return await fetcher.get<Event[]>(`organizations/${organizationId}/events/participated`);
};

const getGuestAnswers = async (eventId: number, guestId: number) => {
  return await fetcher.get<GuestAnswerAPIResponse[]>(`events/${eventId}/guests/${guestId}/answers`);
};
