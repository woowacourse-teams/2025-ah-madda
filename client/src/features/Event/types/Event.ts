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
  maxCapacity: number | '';
  eventStart: string;
  eventEnd: string;
  registrationEnd: string;
  questions: QuestionRequest[];
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

export type Organization = {
  organizationId: number;
  name: string;
  description: string;
  imageUrl: string;
};
