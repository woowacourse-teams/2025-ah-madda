export const mockHostEvents = [
  {
    eventId: 123,
    title: '테스트 이벤트',
    description: '테스트 이벤트 설명',
    organizerName: '홍길동',
    registrationEnd: '2025-12-31T23:59:59',
    eventStart: '2025-01-15T10:00:00',
    eventEnd: '2025-01-15T12:00:00',
    place: '서울시 강남구',
    currentGuestCount: 5,
    maxCapacity: 20,
  },
  {
    eventId: 456,
    title: '두 번째 이벤트',
    description: '두 번째 이벤트 설명',
    organizerName: '김철수',
    registrationEnd: '2025-12-25T23:59:59',
    eventStart: '2025-01-20T14:00:00',
    eventEnd: '2025-01-20T16:00:00',
    place: '부산시 해운대구',
    currentGuestCount: 10,
    maxCapacity: 30,
  },
];

export const mockEventDetail = {
  eventId: 123,
  title: '테스트 이벤트',
  description: '테스트 이벤트 설명',
  organizerName: '홍길동',
  registrationStart: '2025-01-01T00:00:00',
  registrationEnd: '2025-12-31T23:59:59',
  eventStart: '2025-01-15T10:00:00',
  eventEnd: '2025-01-15T12:00:00',
  place: '서울시 강남구',
  currentGuestCount: 5,
  maxCapacity: 20,
  questions: [
    {
      questionId: 1,
      questionText: '참석 동기를 알려주세요',
      isRequired: true,
      orderIndex: 0,
    },
  ],
};

export const mockGuests = [
  {
    guestId: 1,
    organizationMemberId: 1,
    nickname: '참석자1',
  },
  {
    guestId: 2,
    organizationMemberId: 2,
    nickname: '참석자2',
  },
];

export const mockNonGuests = [
  {
    organizationMemberId: 3,
    nickname: '미참석자1',
  },
];
