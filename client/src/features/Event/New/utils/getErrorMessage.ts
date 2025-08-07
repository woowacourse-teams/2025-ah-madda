import { BasicEventFormFields, CreateEventAPIRequest } from '../../types/Event';
import { ERROR_MESSAGES, MAX_LENGTH } from '../constants/errorMessages';

import { VALIDATION_RULES } from './validationRules';
import { isEmpty } from './validators';

export const getErrorMessage = (
  field: keyof CreateEventAPIRequest,
  value: string,
  formData?: BasicEventFormFields
): string => {
  if (field === 'questions') {
    if (!value.trim()) return ERROR_MESSAGES.REQUIRED('질문');
    if (value.length > MAX_LENGTH.QUESTION) {
      return ERROR_MESSAGES.MAX_LENGTH('질문', MAX_LENGTH.QUESTION);
    }
    return '';
  }

  const rule = VALIDATION_RULES[field];
  if (!rule) return '';

  if (rule.required && isEmpty(value)) {
    return ERROR_MESSAGES.REQUIRED(rule.label);
  }

  if (rule.maxLength && value.length > rule.maxLength) {
    return ERROR_MESSAGES.MAX_LENGTH(rule.label, rule.maxLength);
  }

  if (rule.validator) {
    const customMessage = rule.validator(value, formData!);
    if (customMessage) return customMessage;
  }

  return '';
};
