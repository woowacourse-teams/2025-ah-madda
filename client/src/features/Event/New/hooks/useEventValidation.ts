import { useState } from 'react';

import { EventFormData } from '../../types/Event';
import {
  getValidationMessage,
  validateAllFields,
  isFormDataEmpty,
} from '../utils/validateEventForm';

export const useEventValidation = (formData: EventFormData) => {
  const [errors, setErrors] = useState<Partial<Record<keyof EventFormData, string>>>({});

  const validate = () => {
    const newErrors = validateAllFields(formData);
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  const validateField = (field: keyof EventFormData, value: string) => {
    const message = getValidationMessage(field, value, formData);
    setErrors((prev) => ({ ...prev, [field]: message }));
  };

  const isFormValid = Object.values(errors).every((msg) => !msg) && !isFormDataEmpty(formData);

  return {
    errors,
    validate,
    validateField,
    isFormValid,
  };
};
