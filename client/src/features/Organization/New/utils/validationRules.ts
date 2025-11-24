import { IMAGE, MAX_LENGTH } from '../constants/validationRules';

export type OrgFormFields = {
  name: string;
  description: string;
  thumbnail: File | null;
};

type ValidationRule = {
  label: string;
  required?: boolean;
  maxLength?: number;
  maxBytes?: number;
};

export const ORG_VALIDATION_RULES: Record<keyof OrgFormFields, ValidationRule> = {
  name: {
    label: '이벤트 스페이스 이름',
    required: true,
    maxLength: MAX_LENGTH.NAME,
  },
  description: {
    label: '소개',
    required: true,
    maxLength: MAX_LENGTH.DESCRIPTION,
  },
  thumbnail: {
    label: '이벤트 스페이스 이미지',
    required: true,
    maxBytes: IMAGE.MAX_BYTES,
  },
};
