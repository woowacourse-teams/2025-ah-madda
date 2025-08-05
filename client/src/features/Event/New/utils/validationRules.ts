import { BasicEventFormFields } from '../../types/Event';
import { ERROR_MESSAGES, MAX_LENGTH } from '../constants/errorMessages';

import { getMaxEventEndDate, getMaxEventStartDate } from './date';
import { isAfter, isBeforeorEqual, isFutureDate, isPositiveInteger } from './validators';

type ValidationRule = {
  required?: boolean;
  maxLength?: number;
  validator?: (value: string, formData: BasicEventFormFields) => string | null;
  label: string;
};

export const VALIDATION_RULES: Partial<Record<keyof BasicEventFormFields, ValidationRule>> = {
  title: {
    required: true,
    maxLength: MAX_LENGTH.TITLE,
    label: '이벤트 이름',
  },
  eventStart: {
    required: true,
    label: '이벤트 시작 시간',
    validator: (value) => {
      if (!isFutureDate(value)) return ERROR_MESSAGES.EVENT_START_MUST_BE_FUTURE;

      const maxDate = getMaxEventStartDate();
      if (new Date(value) > maxDate) {
        return ERROR_MESSAGES.EVENT_START_MUST_BE_BEFORE_END_OF_NEXT_YEAR;
      }

      return null;
    },
  },
  eventEnd: {
    required: true,
    label: '이벤트 종료 시간',
    validator: (value, formData) => {
      if (isBeforeorEqual(value, formData.eventStart)) {
        return ERROR_MESSAGES.EVENT_END_MUST_BE_AFTER_START;
      }

      const maxEnd = getMaxEventEndDate(formData.eventStart);
      if (new Date(value) > maxEnd) {
        return ERROR_MESSAGES.EVENT_END_MUST_BE_WITHIN_30_DAYS_FROM_START;
      }

      return null;
    },
  },
  registrationEnd: {
    required: true,
    label: '신청 마감 시간',
    validator: (value, formData) => {
      if (!isFutureDate(value)) return ERROR_MESSAGES.EVENT_START_MUST_BE_FUTURE;
      if (isAfter(value, formData.eventStart))
        return ERROR_MESSAGES.REGISTRATION_DEADLINE_BEFORE_EVENT_START;
      return null;
    },
  },
  place: {
    maxLength: MAX_LENGTH.PLACE,
    label: '장소',
  },
  description: {
    maxLength: MAX_LENGTH.DESCRIPTION,
    label: '설명',
  },
  maxCapacity: {
    label: '수용 인원',
    validator: (value) => (!isPositiveInteger(value) ? ERROR_MESSAGES.NOT_POSITIVE_INTEGER : null),
  },
};
