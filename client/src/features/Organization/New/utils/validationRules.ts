import { MAX_LENGTH } from '../constants/errorMessages';

export type OrgFormFields = {
  name: string;
  description: string;
  logo: File | null;
};

type ValidationRule<TForm> = {
  label: string;
  required?: boolean;
  maxLength?: number;
};

export const ORG_VALIDATION_RULES: Record<keyof OrgFormFields, ValidationRule<OrgFormFields>> = {
  name: {
    label: '조직 이름',
    required: true,
    maxLength: MAX_LENGTH.NAME,
  },
  description: {
    label: '소개',
    required: true,
    maxLength: MAX_LENGTH.DESCRIPTION,
  },
  logo: {
    label: '조직 이미지',
    required: true,
  },
};
