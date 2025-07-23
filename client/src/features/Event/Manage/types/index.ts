export type EventInfo = {
  id: string;
  title: string;
  description: string;
  organizer: string;
  location: string;
  deadlineTime: string;
  startTime: string;
  endTime: string;
  currentParticipants: number;
  maxParticipants: number;
};

export type Guest = {
  name: string;
  status: string;
};

export type EventManageData = {
  eventInfo: EventInfo;
  completedGuests: Guest[];
  pendingGuests: Guest[];
};
