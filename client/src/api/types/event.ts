import type { CreateEventAPIRequest } from '@/features/Event/types/Event';

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
