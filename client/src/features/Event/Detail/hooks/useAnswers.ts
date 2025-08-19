import { useState } from 'react';

import { Answer } from '@/api/types/event';

import { Question } from '../../types/Event';

export const useAnswers = (questions: Question[]) => {
  const [answers, setAnswers] = useState<Answer[]>(
    questions.map(({ questionId }) => ({
      questionId,
      answerText: '',
    }))
  );

  const handleChangeAnswer = (questionId: number, answerText: string) => {
    setAnswers((prev) =>
      prev.map((answer) => (answer.questionId === questionId ? { ...answer, answerText } : answer))
    );
  };

  const resetAnswers = () => {
    setAnswers((prev) => prev.map((answer) => ({ ...answer, answerText: '' })));
  };

  return {
    answers,
    handleChangeAnswer,
    resetAnswers,
  };
};
