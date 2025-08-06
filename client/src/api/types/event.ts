export type Answer = {
  questionId: number;
  answerText: string;
};

export type GuestStatusAPIResponse = {
  isGuest: boolean;
};

export type OrganizerStatusAPIResponse = {
  isOrganizer: boolean;
};

export type StatisticsAPIResponse = {
  date: string;
  count: number;
};
