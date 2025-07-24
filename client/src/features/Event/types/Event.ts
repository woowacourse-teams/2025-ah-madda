export type Event = {
  eventId: number;
  title: string;
  description: string;
  eventStart: string;
  eventEnd: string;
  currentGuestCount: string;
  maxCapacity: number;
  place: string;
  registrationStart: string;
  registrationEnd: string;
  organizerName: string;
};

export type EventDetail = {
  eventId: number;
  title: string;
  description: string;
  place: string;
  organizerName: string;
  eventStart: string;
  eventEnd: string;
  registrationStart: string;
  registrationEnd: string;
  currentGuestCount: number;
  maxCapacity: number;
  questions: Question[];
};

export type CreateEventAPIRequest = {
  title: string;
  description: string;
  place: string;
  maxCapacity: number;
  eventStart: string;
  eventEnd: string;
  registrationStart: string;
  registrationEnd: string;
  organizerNickname: string;
  questions: QuestionRequest[];
};

export type Question = {
  questionId: number;
  questionText: string;
  isRequired: boolean;
  orderIndex: number;
};

export type QuestionRequest = {
  questionText: string;
  isRequired: boolean;
  orderIndex: number;
};

export type Organization = {
  organizationId: number;
  name: string;
  description: string;
  imageUrl: string;
};
