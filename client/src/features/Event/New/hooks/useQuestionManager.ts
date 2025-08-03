import { useReducer, useMemo } from 'react';

import { QuestionRequest } from '../../types/Event';
import { MAX_LENGTH, MAX_QUESTIONS } from '../constants/errorMessages';

type Action =
  | { type: 'ADD' }
  | { type: 'DELETE'; index: number }
  | { type: 'UPDATE'; index: number; data: Partial<QuestionRequest> };

const reducer = (state: QuestionRequest[], action: Action): QuestionRequest[] => {
  switch (action.type) {
    case 'ADD': {
      const newQuestion: QuestionRequest = {
        orderIndex: state.length,
        isRequired: false,
        questionText: '',
      };
      return [...state, newQuestion];
    }
    case 'DELETE': {
      return state.filter((_, i) => i !== action.index).map((q, i) => ({ ...q, orderIndex: i }));
    }
    case 'UPDATE': {
      return state.map((q, i) => (i === action.index ? { ...q, ...action.data } : q));
    }
    default:
      return state;
  }
};

export const useQuestionManager = () => {
  const [questions, dispatch] = useReducer(reducer, []);
  const canAddQuestion = questions.length < MAX_QUESTIONS;

  const addQuestion = () => {
    if (!canAddQuestion) {
      alert('질문은 최대 5개까지 추가 가능합니다.');
      return;
    }
    dispatch({ type: 'ADD' });
  };
  const deleteQuestion = (index: number) => dispatch({ type: 'DELETE', index });
  const updateQuestion = (index: number, data: Partial<QuestionRequest>) =>
    dispatch({ type: 'UPDATE', index, data });

  const isValid = useMemo(() => {
    return questions.every((q) => {
      if (!q.questionText.trim()) return false;
      if (q.questionText.length > MAX_LENGTH.QUESTION) return false;
      return true;
    });
  }, [questions]);

  return {
    questions,
    addQuestion,
    deleteQuestion,
    updateQuestion,
    isValid,
  };
};
