import { useState } from 'react';

import { EventFormData } from '../../types/Event';
import { ERROR_MESSAGES } from '../constants/errorMessages';
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
        newQuestionErrors[i] = ERROR_MESSAGES.REQUIRED('질문');
      }
    });
    setQuestionErrors(newQuestionErrors);

    const isValidQuestions =
      formData.questions.length === 0 ||
      Object.values(newQuestionErrors).every((msg) => msg === '');

    const isValidForm = Object.keys(newErrors).length === 0 && isValidQuestions;

    return isValidForm;
  };

  const validateField = (field: keyof EventFormData, value: string) => {
    const message = getValidationMessage(field, value, formData);
    setErrors((prev) => ({ ...prev, [field]: message }));
  };

  const isFormValid =
    !Object.values(errors).some((msg) => !!msg) &&
    !isFormDataEmpty(formData) &&
    !(formData.questions.length > 0 && Object.values(questionErrors).some((msg) => !!msg));

  return {
    errors,
    questionErrors,
    setQuestionErrors,
    validate,
    validateField,
    isFormValid,
  };
};
