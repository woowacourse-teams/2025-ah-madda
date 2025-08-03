import { EventFormData } from '../../types/Event';

import { getValidationMessage } from './getErrorMessage';
import { VALIDATION_RULES } from './validationRules';

export const validateAllFields = (
  formData: EventFormData
): Partial<Record<keyof EventFormData, string>> => {
  const newErrors: Partial<Record<keyof EventFormData, string>> = {};

  Object.entries(formData).forEach(([key, value]) => {
    const msg = getValidationMessage(key as keyof EventFormData, value.toString(), formData);
    if (msg) newErrors[key as keyof EventFormData] = msg;
  });

  return newErrors;
};

export const isFormDataEmpty = (formData: EventFormData): boolean => {
  const requiredFields: (keyof EventFormData)[] = Object.entries(VALIDATION_RULES)
    .filter(([, rule]) => rule.required)
    .map(([field]) => field) as (keyof EventFormData)[];

  return requiredFields.some((key) => {
    const value = formData[key];
    return value?.toString().trim() === '';
  });
};
