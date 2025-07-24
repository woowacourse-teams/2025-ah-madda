import { queryOptions } from '@tanstack/react-query';

import { Guest, NonGuest } from '@/features/Event/Manage/types';
import { EventDetail } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';

export const eventQueryKeys = {
  all: () => ['event'],
  detail: () => [...eventQueryKeys.all(), 'detail'],
  alarm: () => [...eventQueryKeys.all(), 'alarm'],
  guests: () => [...eventQueryKeys.all(), 'guests'],
  nonGuests: () => [...eventQueryKeys.all(), 'nonGuests'],
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

export const guestManageQueryOptions = {
  guests: (eventId: number) => ({
    queryKey: [...eventQueryKeys.guests(), eventId],
    queryFn: () => getGuests(eventId),
  }),
  nonGuests: (eventId: number) => ({
    queryKey: [...eventQueryKeys.nonGuests(), eventId],
    queryFn: () => getNonGuests(eventId),
  }),
};

const getGuests = async (eventId: number) => {
  const response = await fetcher.get<Guest[]>(`events/${eventId}/guests`);
  return response;
};

const getNonGuests = async (eventId: number) => {
  const response = await fetcher.get<NonGuest[]>(`events/${eventId}/non-guests`);
  return response;
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
