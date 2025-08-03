import { BasicEventFormFields } from '../../types/Event';

import { getValidationMessage } from './getErrorMessage';

export const validateEventForm = (
  formData: BasicEventFormFields
): Partial<Record<keyof BasicEventFormFields, string>> => {
  const newErrors: Partial<Record<keyof BasicEventFormFields, string>> = {};

  Object.entries(formData).forEach(([key, value]) => {
    const msg = getValidationMessage(key as keyof BasicEventFormFields, value.toString(), formData);
    if (msg) newErrors[key as keyof BasicEventFormFields] = msg;
  });

  return newErrors;
};
