import { queryOptions } from '@tanstack/react-query';

import { Guest, NonGuest } from '@/features/Event/Manage/types';
import { CreateEventAPIRequest, EventDetail } from '@/features/Event/types/Event';

import { fetcher } from '../fetcher';
import { postAlarm } from '../mutations/useAddAlarm';

type CreateEventAPIResponse = {
  eventId: number;
};

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
  alarms: (eventId: number) => ({
    mutationKey: [...eventQueryKeys.alarm(), eventId],
    mutationFn: (content: string) => postAlarm(eventId, content),
  }),
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

export const createEventAPI = (organizationId: number, data: CreateEventAPIRequest) => {
  return fetcher.post<CreateEventAPIResponse>(`organizations/${organizationId}/events`, {
    json: data,
  });
};

const getEventDetailAPI = (eventId: number) => {
  return fetcher.get<EventDetail>(`organizations/events/${eventId}`);
};
