export const MAX_LENGTH = {
  TITLE: 140,
  DESCRIPTION: 10_000,
  PLACE: 100,
  QUESTION: 150,
  ANSWER: 1_000,
};

export const MAX_QUESTIONS = 5;
export const UNLIMITED_CAPACITY = 2_000_000_000;

export const ERROR_MESSAGES = {
  REQUIRED: (label: string) => `${label}을(를) 입력해 주세요.`,
  MAX_LENGTH: (label: string, max: number) => `${label}은(는) ${max}자 이하로 입력해 주세요.`,
  EVENT_START_MUST_BE_FUTURE: '이벤트 시작 시간은 현재 시간 이후여야 합니다.',
  EVENT_END_MUST_BE_AFTER_START: '이벤트 종료 시간은 시작 시간 이후여야 합니다.',
  REGISTRATION_DEADLINE_BEFORE_EVENT_START: '이벤트 신청 종료 시간은 시작 시간 이전이어야 합니다.',
  NOT_POSITIVE_INTEGER: '1 이상의 정수를 입력해 주세요.',
};
