export const MAX_LENGTH = {
  NAME: 30,
  DESCRIPTION: 30,
};

export const ERROR_MESSAGES = {
  REQUIRED: (label: string) => `${label}을(를) 입력해 주세요.`,
  MAX_LENGTH: (label: string, max: number) => `${label}은(는) ${max}자 이하로 입력해 주세요.`,
};
