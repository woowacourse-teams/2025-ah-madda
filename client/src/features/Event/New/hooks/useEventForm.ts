import { useState } from 'react';

import type { CreateEventAPIRequest, QuestionRequest } from '@/features/Event/types/Event';

const initialFormData: Omit<CreateEventAPIRequest, 'organizerNickname'> = {
  title: '',
  description: '',
  place: '',
  eventStart: '',
  eventEnd: '',
  registrationEnd: '',
  maxCapacity: 0,
  questions: [],
};

export const useEventForm = () => {
  const [formData, setFormData] = useState(initialFormData);

  const setValue = <K extends keyof typeof initialFormData>(key: K, value: string | number) => {
    setFormData((prev) => ({ ...prev, [key]: value }));
  };

  const setQuestions = (questions: QuestionRequest[]) => {
    setFormData((prev) => ({
      ...prev,
      questions,
    }));
  };

  return {
    formData,
    setFormData,
    setValue,
    setQuestions,
  };
};
