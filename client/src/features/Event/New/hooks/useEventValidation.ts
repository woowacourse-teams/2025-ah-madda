import { useState } from 'react';

import { EventFormData } from '../../types/Event';
import { VALIDATION_MESSAGES } from '../constants/validation';
import {
  getValidationMessage,
  validateAllFields,
  isFormDataEmpty,
} from '../utils/validateEventForm';

export const useEventValidation = (formData: EventFormData) => {
  const [errors, setErrors] = useState<Partial<Record<keyof EventFormData, string>>>({});
  const [questionErrors, setQuestionErrors] = useState<Record<number, string>>({});

  const validate = () => {
    const newErrors = validateAllFields(formData);
    setErrors(newErrors);

    const newQuestionErrors: Record<number, string> = {};
    formData.questions.forEach((q, i) => {
      if (q.questionText.trim() === '') {
        newQuestionErrors[i] = VALIDATION_MESSAGES.REQUIRED('질문');
      }
    });
    setQuestionErrors(newQuestionErrors);

    const isValidQuestions = Object.values(newQuestionErrors).every((msg) => msg === '');

    return Object.keys(newErrors).length === 0 && isValidQuestions;
  };

  const validateField = (field: keyof EventFormData, value: string) => {
    const message = getValidationMessage(field, value, formData);
    setErrors((prev) => ({ ...prev, [field]: message }));
  };

  const isFormValid =
    Object.values(errors).every((msg) => !msg) &&
    Object.values(questionErrors).every((msg) => !msg) &&
    !isFormDataEmpty(formData);

  return {
    errors,
    questionErrors,
    setQuestionErrors,
    validate,
    validateField,
    isFormValid,
  };
};
