import { CreateEventAPIRequest, Question } from '@/features/Event/types/Event';

export type Answer = {
  questionId: number;
  answerText: string;
};

export type PastEventAPIResponse = {
  organizationId: number;
  lastEventId?: number;
};

export type GuestStatusAPIResponse = {
  isGuest: boolean;
};

export type OrganizerStatusAPIResponse = {
  isOrganizer: boolean;
};

export type UpdateEventParams = {
  eventId: number;
  payload: CreateEventAPIRequest;
};

export type UpdateEventResponse = {
  eventId: number;
};

export type StatisticsAPIResponse = {
  date: string;
  count: number;
};

export type EventTitleAPIResponse = {
  eventId: number;
  title: string;
};

export type EventTemplateAPIResponse = Pick<
  CreateEventAPIRequest,
  'title' | 'description' | 'maxCapacity' | 'place'
> & {
  eventId: number;
};

export type NotifyHistoryAPIResponse = {
  recipientCount: number;
  content: string;
  sentAt: string;
};

export type TemplateAPIRequest = {
  title: string;
  description: string;
};

export type TemplateListAPIResponse = {
  templateId: number;
  title: string;
};

export type TemplateDetailAPIResponse = {
  templateId: number;
  description: string;
};

export type Event = {
  eventId: number;
  title: string;
  description: string;
  eventStart: string;
  eventEnd: string;
  currentGuestCount: number;
  maxCapacity: number;
  place: string;
  registrationStart: string;
  registrationEnd: string;
  organizerNicknames: string[];
  isGuest?: boolean;
  isOrganizer?: boolean;
};

export type EventDetail = {
  eventId: number;
  title: string;
  description: string;
  place: string;
  organizerNicknames: string[];
  eventOrganizerIds: number[];
  eventStart: string;
  eventEnd: string;
  registrationStart: string;
  registrationEnd: string;
  currentGuestCount: number;
  maxCapacity: number;
  questions: Question[];
};
