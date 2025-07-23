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

// S.TODO: 추후 Organization 폴더가 생기면 이동
export type Organization = {
  organizationId: number;
  name: string;
  description: string;
  imageUrl: string;
};
