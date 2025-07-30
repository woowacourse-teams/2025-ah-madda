import { useState } from 'react';

import type { CreateEventAPIRequest, QuestionRequest } from '@/features/Event/types/Event';

import { UNLIMITED_CAPACITY } from '../constants/validation';

export const useEventForm = () => {
  const [formData, setFormData] = useState<Omit<CreateEventAPIRequest, 'organizerNickname'>>({
    title: '',
    description: '',
    place: '',
    eventStart: '',
    eventEnd: '',
    registrationEnd: '',
    maxCapacity: UNLIMITED_CAPACITY,
    questions: [],
  });

  const handleChange =
    (key: keyof CreateEventAPIRequest) => (e: React.ChangeEvent<HTMLInputElement>) => {
      const value = key === 'maxCapacity' ? Number(e.target.value) : e.target.value;
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
    handleChange,
    setQuestions,
  };
};
