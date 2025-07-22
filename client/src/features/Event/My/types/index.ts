export type Event = {
  id: string;
  title: string;
  description: string;
  author: string;
  deadlineTime: string;
  startTime: string;
  endTime: string;
  location: string;
  currentParticipants: number;
  maxParticipants: number;
  type: 'host' | 'participate';
};

export type EventsResponse = {
  hostEvents: Event[];
  participateEvents: Event[];
};
