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
  isGuest: boolean;
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

export type CreateEventAPIRequest = {
  title: string;
  description: string;
  place: string;
  maxCapacity: number | '';
  eventStart: string;
  eventEnd: string;
  registrationEnd: string;
  questions: QuestionRequest[];
  eventOrganizerIds: number[];
  groupIds: number[];
};

export type EventFormData = Omit<CreateEventAPIRequest, 'organizerNickname'>;
export type BasicEventFormFields = Omit<CreateEventAPIRequest, 'questions'>;

export type Question = {
  questionId: number;
} & QuestionRequest;

export type QuestionRequest = {
  questionText: string;
  isRequired: boolean;
  orderIndex: number;
};
