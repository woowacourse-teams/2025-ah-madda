import { EventFormData } from '../../types/Event';

import { getValidationMessage } from './getErrorMessage';

export const validateEventForm = (
  formData: EventFormData
): Partial<Record<keyof EventFormData, string>> => {
  const newErrors: Partial<Record<keyof EventFormData, string>> = {};

  Object.entries(formData).forEach(([key, value]) => {
    const msg = getValidationMessage(key as keyof EventFormData, value.toString(), formData);
    if (msg) newErrors[key as keyof EventFormData] = msg;
  });

  return newErrors;
};
