import { useState } from 'react';

import type { CreateEventAPIRequest, QuestionRequest } from '@/features/Event/types/Event';

export const useEventForm = () => {
  const [formData, setFormData] = useState<Omit<CreateEventAPIRequest, 'organizerNickname'>>({
    title: '',
    description: '',
    place: '',
    eventStart: '',
    eventEnd: '',
    registrationEnd: '',
    maxCapacity: 0,
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
