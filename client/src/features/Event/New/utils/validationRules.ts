import { BasicEventFormFields } from '../../types/Event';
import { ERROR_MESSAGES, MAX_LENGTH } from '../constants/errorMessages';

import { isAfterorEqual, isBefore, isFutureDate, isPositiveInteger } from './validators';

type ValidationRule = {
  required?: boolean;
  maxLength?: number;
  validator?: (value: string, formData: BasicEventFormFields) => string | null;
  label: string;
};

export const VALIDATION_RULES: Partial<Record<keyof BasicEventFormFields, ValidationRule>> = {
  title: {
    required: true,
    maxLength: MAX_LENGTH,
    label: '이벤트 이름',
  },
  eventStart: {
    required: true,
    label: '이벤트 시작 시간',
    validator: (value) => (!isFutureDate(value) ? ERROR_MESSAGES.EVENT_START_MUST_BE_FUTURE : null),
  },
  eventEnd: {
    required: true,
    label: '이벤트 종료 시간',
    validator: (value, formData) =>
      isBefore(value, formData.eventStart) ? ERROR_MESSAGES.EVENT_END_MUST_BE_AFTER_START : null,
  },
  registrationEnd: {
    required: true,
    label: '신청 마감 시간',
    validator: (value, formData) => {
      if (!isFutureDate(value)) return ERROR_MESSAGES.EVENT_START_MUST_BE_FUTURE;
      if (isAfterorEqual(value, formData.eventStart))
        return ERROR_MESSAGES.REGISTRATION_DEADLINE_BEFORE_EVENT_START;
      return null;
    },
  },
  place: {
    required: true,
    maxLength: MAX_LENGTH,
    label: '장소',
  },
  description: {
    required: true,
    maxLength: MAX_LENGTH,
    label: '설명',
  },
  maxCapacity: {
    label: '수용 인원',
    validator: (value) => (!isPositiveInteger(value) ? ERROR_MESSAGES.NOT_POSITIVE_INTEGER : null),
  },
};
