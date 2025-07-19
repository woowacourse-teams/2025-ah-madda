export type Event = {
  id: string;
  title: string;
  description: string;
  author: string;
  deadlineTime: string; // 신청 마감 시간
  startTime: string; // 시작 시간
  endTime: string; // 종료 시간
  location: string;
  currentParticipants: number; // 현재 참여자 수
  maxParticipants: number; // 최대 참여자 수
  type: 'host' | 'participate'; // 주최 이벤트인지 참여 이벤트인지
};

export type EventsResponse = {
  hostEvents: Event[];
  participateEvents: Event[];
};
