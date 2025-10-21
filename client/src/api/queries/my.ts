import { queryOptions } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { GuestAnswerAPIResponse, ParticipateEventAPIResponse } from '../types/my';

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
    hostEvents: () =>
      queryOptions({
        queryKey: [...myQueryKeys.event.host()],
        queryFn: () => getHostEvents(),
      }),
    participateEvents: () =>
      queryOptions({
        queryKey: [...myQueryKeys.event.participate()],
        queryFn: () => getParticipateEvents(),
      }),
    guestAnswers: (eventId: number, guestId: number) =>
      queryOptions({
        queryKey: [...myQueryKeys.event.guestAnswers(), eventId, guestId],
        queryFn: () => getGuestAnswers(eventId, guestId),
      }),
  },
};

const getHostEvents = async () => {
  return await fetcher.get<ParticipateEventAPIResponse[]>(`members/events/owned`);
};

const getParticipateEvents = async () => {
  return await fetcher.get<ParticipateEventAPIResponse[]>(`members/events/participated`);
};

const getGuestAnswers = async (eventId: number, guestId: number) => {
  return await fetcher.get<GuestAnswerAPIResponse[]>(`events/${eventId}/guests/${guestId}/answers`);
};
