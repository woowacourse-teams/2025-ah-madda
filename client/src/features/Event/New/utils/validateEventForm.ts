import { EventFormData } from '../../types/Event';
import { FIELD_CONFIG } from '../constants/formFieldConfig';

import { getValidationMessage } from './getErrorMessage';

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
  return Object.entries(FIELD_CONFIG).some(([key, config]) => {
    if (!config.required) return false;

    const value = formData[key as keyof EventFormData];
    return typeof value === 'string' ? value.trim() === '' : value == null;
  });
};
