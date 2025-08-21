import { ERROR_MESSAGES } from '../constants/errorMessages';

import { ORG_VALIDATION_RULES, OrgFormFields } from './validationRules';
import { isEmpty } from './validators';

export const getOrgErrorMessage = (
  field: keyof OrgFormFields,
  value: OrgFormFields[typeof field]
): string => {
  const rule = ORG_VALIDATION_RULES[field];

  if (rule.required && isEmpty(value)) {
    return ERROR_MESSAGES.REQUIRED(rule.label);
  }

  if (rule.maxLength && typeof value === 'string') {
    const v = value.trim();
    if (v.length > rule.maxLength) {
      return ERROR_MESSAGES.MAX_LENGTH(rule.label, rule.maxLength);
    }
  }

  return '';
};
