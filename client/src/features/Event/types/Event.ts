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
  questions: Questions[];
};

type Questions = {
  questionId: number;
  questionText: string;
  isRequired: boolean;
  orderIndex: number;
};

// S.TODO: 추후 Organization 폴더가 생기면 이동
export type Organization = {
  organizationId: number;
  name: string;
  description: string;
  imageUrl: string;
};
