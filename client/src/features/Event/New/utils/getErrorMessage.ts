import { BasicEventFormFields } from '../../types/Event';
import { ERROR_MESSAGES } from '../constants/errorMessages';

import { VALIDATION_RULES } from './validationRules';
import { isEmpty } from './validators';

export const getValidationMessage = (
  field: keyof BasicEventFormFields,
  value: string,
  formData: BasicEventFormFields
): string => {
  const rule = VALIDATION_RULES[field];
  if (!rule) return '';

  if (rule.required && isEmpty(value)) {
    return ERROR_MESSAGES.REQUIRED(rule.label);
  }

  if (rule.maxLength && value.length > rule.maxLength) {
    return ERROR_MESSAGES.MAX_LENGTH(rule.label, rule.maxLength);
  }

  if (rule.validator) {
    const customMessage = rule.validator(value, formData);
    if (customMessage) return customMessage;
  }

  return '';
};
