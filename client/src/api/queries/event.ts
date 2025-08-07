import { queryOptions } from '@tanstack/react-query';

import { Guest, NonGuest } from '../../features/Event/Manage/types';
import { CreateEventAPIRequest, EventDetail } from '../../features/Event/types/Event';
import { fetcher } from '../fetcher';
import { postAlarm } from '../mutations/useAddAlarm';
import { GuestStatusAPIResponse, OrganizerStatusAPIResponse } from '../types/event';
import { NotificationAPIRequest } from '../types/notification';

type CreateEventAPIResponse = {
  eventId: number;
};

export const eventQueryKeys = {
  all: () => ['event'],
  detail: () => [...eventQueryKeys.all(), 'detail'],
  alarm: () => [...eventQueryKeys.all(), 'alarm'],
  guests: () => [...eventQueryKeys.all(), 'guests'],
  organizer: () => [...eventQueryKeys.all(), 'organizer'],
  nonGuests: () => [...eventQueryKeys.all(), 'nonGuests'],
  guestStatus: () => [...eventQueryKeys.all(), 'guestStatus'],
  participation: () => [...eventQueryKeys.all(), 'participation'],
  cancel: () => [...eventQueryKeys.all(), 'cancel'],
};

export const eventQueryOptions = {
  detail: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.detail(), eventId],
      queryFn: () => getEventDetailAPI(eventId),
    }),
  alarms: (eventId: number) => ({
    mutationKey: [...eventQueryKeys.alarm(), eventId],
    mutationFn: (data: NotificationAPIRequest) => postAlarm(eventId, data),
  }),
  guests: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.guests(), eventId],
      queryFn: () => getGuests(eventId),
    }),
  organizer: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.organizer(), eventId],
      queryFn: () => getOrganizerStatus(eventId),
    }),
  nonGuests: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.nonGuests(), eventId],
      queryFn: () => getNonGuests(eventId),
    }),
  guestStatus: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.guestStatus(), eventId],
      queryFn: () => getGuestStatus(eventId),
    }),
  cancel: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.cancel(), eventId],
      queryFn: () => fetcher.delete(`events/${eventId}`),
    }),
};

const getGuests = async (eventId: number) => {
  return await fetcher.get<Guest[]>(`events/${eventId}/guests`);
};

const getNonGuests = async (eventId: number) => {
  return await fetcher.get<NonGuest[]>(`events/${eventId}/non-guests`);
};

export const createEventAPI = (organizationId: number, data: CreateEventAPIRequest) => {
  return fetcher.post<CreateEventAPIResponse>(`organizations/${organizationId}/events`, data);
};

export const getEventDetailAPI = (eventId: number) => {
  return fetcher.get<EventDetail>(`organizations/events/${eventId}`);
};

export const updateEventAPI = (eventId: number, data: CreateEventAPIRequest) => {
  return fetcher.patch<void>(`organizations/events/${eventId}`, data);
};

const getGuestStatus = async (eventId: number) => {
  return await fetcher.get<GuestStatusAPIResponse>(`events/${eventId}/guest-status`);
};

const getOrganizerStatus = async (eventId: number) => {
  return await fetcher.get<OrganizerStatusAPIResponse>(
    `organizations/events/${eventId}/organizer-status`
  );
};
