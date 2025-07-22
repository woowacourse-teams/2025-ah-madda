export type EventDetail = {
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
  preQuestions: string[];
};
