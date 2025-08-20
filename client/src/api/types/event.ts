import { CreateEventAPIRequest } from '@/features/Event/types/Event';

export type Answer = {
  questionId: number;
  answerText: string;
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
