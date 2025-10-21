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
