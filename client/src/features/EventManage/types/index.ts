export type EventInfo = {
  id: string;
  title: string;
  description: string;
  organizer: string; // 주최자
  location: string;
  deadlineTime: string; // 신청 마감
  startTime: string; // 이벤트 시작 시간
  endTime: string; // 이벤트 종료 시간
  currentParticipants: number; // 현재 참가자 수
  maxParticipants: number; // 최대 참가자 수
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
