import { CreateEventAPIRequest } from '@/features/Event/types/Event';

export type Answer = {
  questionId: number;
  answerText: string;
};

export type GuestStatusAPIResponse = {
  isGuest: boolean;
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
