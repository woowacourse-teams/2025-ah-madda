export const UI_LABELS = {
  PAGE_TITLE: '내 이벤트',

  HOST_TAB: '주최 이벤트',
  PARTICIPATE_TAB: '참여 이벤트',

  ONGOING_HOST_EVENTS: '진행 중인 이벤트',
  PARTICIPATING_EVENTS: '참여 중인 이벤트',
} as const;

export const STATUS_MESSAGES = {
  LOADING_EVENTS: '이벤트를 불러오는 중...',

  NO_HOST_EVENTS: '주최한 이벤트가 없습니다.',
  NO_PARTICIPATE_EVENTS: '참여한 이벤트가 없습니다.',

  FETCH_ERROR: '이벤트를 불러오는데 실패했습니다.',
} as const;

export const EVENT_CARD_LABELS = {
  DEADLINE_PREFIX: '신청 마감',
  EVENT_TIME_PREFIX: '이벤트 시간',
  LOCATION_PREFIX: '장소',
  PARTICIPANTS_SUFFIX: '명 참여',
} as const;

export const TAB_VALUES = {
  HOST: 'host',
  PARTICIPATE: 'participate',
} as const;
