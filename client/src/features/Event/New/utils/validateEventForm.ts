import { EventFormData } from '../../types/Event';
import { MAX_LENGTH, VALIDATION_MESSAGES } from '../constants/validation';

const isEmpty = (value: string) => value.trim() === '';
const isTooLong = (value: string) => value.length > MAX_LENGTH;
const isFutureDate = (value: string) => new Date(value) > new Date();
const isAfterorEqual = (a: string, b: string) => new Date(a) >= new Date(b);
const isBefore = (a: string, b: string) => new Date(a) < new Date(b);
const isPositiveInteger = (value: string) => /^\d+$/.test(value) && parseInt(value) > 0;

export const getValidationMessage = (
  field: keyof EventFormData,
  value: string,
  formData: EventFormData
): string => {
  switch (field) {
    case 'title':
      if (isEmpty(value)) return VALIDATION_MESSAGES.REQUIRED('이벤트 이름');
      if (isTooLong(value)) return VALIDATION_MESSAGES.MAX_LENGTH('이벤트 이름', MAX_LENGTH);
      break;
    case 'eventStart':
      if (!isFutureDate(value)) return VALIDATION_MESSAGES.EVENT_START_MUST_BE_FUTURE;
      break;
    case 'eventEnd':
      if (isBefore(value, formData.eventStart))
        return VALIDATION_MESSAGES.EVENT_END_MUST_BE_AFTER_START;
      break;
    case 'registrationEnd':
      if (!isFutureDate(value)) return VALIDATION_MESSAGES.EVENT_START_MUST_BE_FUTURE;
      if (isAfterorEqual(value, formData.eventStart))
        return VALIDATION_MESSAGES.REGISTRATION_DEADLINE_BEFORE_EVENT_START;
      break;
    case 'place':
      if (isEmpty(value)) return VALIDATION_MESSAGES.REQUIRED('장소');
      if (isTooLong(value)) return VALIDATION_MESSAGES.MAX_LENGTH('장소', MAX_LENGTH);
      break;
    case 'description':
      if (isEmpty(value)) return VALIDATION_MESSAGES.REQUIRED('설명');
      if (isTooLong(value)) return VALIDATION_MESSAGES.MAX_LENGTH('설명', MAX_LENGTH);
      break;
    case 'maxCapacity':
      if (!isPositiveInteger(value)) return VALIDATION_MESSAGES.NOT_POSITIVE_INTEGER;
      break;
  }

  return '';
};

export const validateAllFields = (
  formData: EventFormData
): Partial<Record<keyof EventFormData, string>> => {
  const newErrors: Partial<Record<keyof EventFormData, string>> = {};

  (Object.entries(formData) as [keyof EventFormData, string | number][]).forEach(([key, value]) => {
    const msg = getValidationMessage(key, value.toString(), formData);
    if (msg) newErrors[key] = msg;
  });

  return newErrors;
};

export const isFormDataEmpty = (formData: EventFormData): boolean => {
  const requiredFields: (keyof EventFormData)[] = [
    'title',
    'eventStart',
    'eventEnd',
    'registrationEnd',
    'place',
    'description',
  ];

  return requiredFields.some((key) => {
    const value = formData[key];
    return value?.toString().trim() === '';
  });
};
