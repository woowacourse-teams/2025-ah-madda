import { Event } from './event';
import { OrganizationAPIResponse } from './organizations';
export type GuestAnswerAPIResponse = {
  questionId: number;
  questionText: string;
  answerId: number;
  answerText: string;
  orderIndex: number;
};

export type ParticipateEventAPIResponse = Omit<Event, 'isGuest'> & OrganizationAPIResponse;
