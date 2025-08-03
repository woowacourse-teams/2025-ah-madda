import { useMemo, useState } from 'react';

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

  const validateFields = () => {
    const newErrors = validateAllFields(formData);
    setErrors(newErrors);
    return newErrors;
  };

  const validateQuestions = () => {
    const newQuestionErrors: Record<number, string> = {};
    formData.questions.forEach((q, i) => {
      if (!q.questionText || q.questionText.trim() === '') {
        newQuestionErrors[i] = ERROR_MESSAGES.REQUIRED('질문');
      }
    });
    setQuestionErrors(newQuestionErrors);
    return newQuestionErrors;
  };

  const isAllValid = (
    fieldErrors: Partial<Record<keyof EventFormData, string>>,
    questionErrors: Record<number, string>
  ) => {
    const hasFieldErrors = Object.keys(fieldErrors).length > 0;
    const hasInvalidQuestions =
      formData.questions.length > 0 && Object.values(questionErrors).some((msg) => !!msg);
    return !hasFieldErrors && !hasInvalidQuestions;
  };

  const validate = () => {
    const fieldErrors = validateFields();
    const newQuestionErrors = validateQuestions();
    return isAllValid(fieldErrors, newQuestionErrors);
  };

  const validateField = (field: keyof EventFormData, value: string) => {
    const message = getValidationMessage(field, value, formData);
    setErrors((prev) => ({ ...prev, [field]: message }));
  };

  const isFormValid = useMemo(() => {
    const hasFieldErrors = Object.values(errors).some((msg) => !!msg);
    const hasEmptyFields = isFormDataEmpty(formData);
    const hasInvalidQuestions =
      formData.questions.length > 0 && Object.values(questionErrors).some((msg) => !!msg);

    return !hasFieldErrors && !hasEmptyFields && !hasInvalidQuestions;
  }, [errors, questionErrors, formData]);

  return {
    errors,
    questionErrors,
    setQuestionErrors,
    validate,
    validateField,
    isFormValid,
  };
};
