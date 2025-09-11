import { queryOptions } from '@tanstack/react-query';

import { Guest, NonGuest } from '../../features/Event/Manage/types';
import { CreateEventAPIRequest, Event, EventDetail } from '../../features/Event/types/Event';
import { fetcher } from '../fetcher';
import { postAlarm } from '../mutations/useAddAlarm';
import {
  GuestStatusAPIResponse,
  OrganizerStatusAPIResponse,
  StatisticsAPIResponse,
  EventTemplateAPIResponse,
  EventTitleAPIResponse,
  NotifyHistoryAPIResponse,
  TemplateListAPIResponse,
  TemplateDetailAPIResponse,
} from '../types/event';
import { NotificationAPIRequest } from '../types/notification';

type CreateEventAPIResponse = {
  eventId: number;
};

export type NotificationOptOutState = { optedOut: boolean };

export const eventQueryKeys = {
  all: () => ['event'],
  ongoing: () => [...eventQueryKeys.all(), 'ongoing'],
  past: () => [...eventQueryKeys.all(), 'past'],
  detail: () => [...eventQueryKeys.all(), 'detail'],
  alarm: () => [...eventQueryKeys.all(), 'alarm'],
  guests: () => [...eventQueryKeys.all(), 'guests'],
  organizer: () => [...eventQueryKeys.all(), 'organizer'],
  nonGuests: () => [...eventQueryKeys.all(), 'nonGuests'],
  guestStatus: () => [...eventQueryKeys.all(), 'guestStatus'],
  participation: () => [...eventQueryKeys.all(), 'participation'],
  cancel: () => [...eventQueryKeys.all(), 'cancel'],
  statistic: () => [...eventQueryKeys.all(), 'statistic'],
  titles: () => [...eventQueryKeys.all(), 'titles'],
  pastEventList: () => [...eventQueryKeys.all(), 'pastEventList'],
  history: () => [...eventQueryKeys.all(), 'history'],
  notificationOptOutState: () => [...eventQueryKeys.all(), 'notificationOptOutState'],
  templateList: () => [...eventQueryKeys.all(), 'templateList'],
  templateDetail: () => [...eventQueryKeys.all(), 'templateDetail'],
};

export const eventQueryOptions = {
  detail: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.detail(), eventId],
      queryFn: () => getEventDetailAPI(eventId),
    }),
  ongoing: (organizationId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.ongoing(), organizationId],
      queryFn: () => getOngoingEventAPI({ organizationId }),
    }),
  past: (organizationId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.past(), organizationId],
      queryFn: () => getPastEventAPI({ organizationId }),
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
      retry: false,
    }),
  cancel: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.cancel(), eventId],
      queryFn: () => fetcher.delete(`events/${eventId}`),
    }),
  statistic: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.statistic(), eventId],
      queryFn: () => fetcher.get<StatisticsAPIResponse[]>(`events/${eventId}/statistic`),
    }),
  titles: (organizationId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.titles(), organizationId],
      queryFn: () => getEventTitles(organizationId),
    }),
  pastEventList: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.pastEventList(), eventId],
      queryFn: () => getPastEventList(eventId),
    }),
  history: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.history(), eventId],
      queryFn: () => getNotifyHistory(eventId),
    }),
  notificationOptOutState: (eventId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.notificationOptOutState(), eventId],
      queryFn: () => getNotificationOptOutState(eventId),
    }),
  templateList: () =>
    queryOptions({
      queryKey: [...eventQueryKeys.templateList()],
      queryFn: () => getTemplateList(),
    }),
  templateDetail: (templateId: number) =>
    queryOptions({
      queryKey: [...eventQueryKeys.templateDetail(), templateId],
      queryFn: () => getTemplateDetail(templateId),
    }),
};

const getOngoingEventAPI = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<Event[]>(`organizations/${organizationId}/events`);
};

const getPastEventAPI = ({ organizationId }: { organizationId: number }) => {
  return fetcher.get<Event[]>(`organizations/${organizationId}/events/past`);
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

const getEventTitles = async (organizationId: number) => {
  return await fetcher.get<EventTitleAPIResponse[]>(
    `organizations/${organizationId}/events/owned/titles`
  );
};

const getPastEventList = async (eventId: number) => {
  return await fetcher.get<EventTemplateAPIResponse>(
    `organizations/events/${eventId}/owned/template`
  );
};

const getNotifyHistory = async (eventId: number) => {
  return await fetcher.get<NotifyHistoryAPIResponse[]>(`events/${eventId}/notification/history`);
};

const getNotificationOptOutState = async (eventId: number) => {
  return await fetcher.get<NotificationOptOutState>(`events/${eventId}/notification/opt-out`);
};

const getTemplateList = async () => {
  return await fetcher.get<TemplateListAPIResponse[]>(`templates`);
};

const getTemplateDetail = async (templateId: number) => {
  return await fetcher.get<TemplateDetailAPIResponse>(`templates/${templateId}`);
};
