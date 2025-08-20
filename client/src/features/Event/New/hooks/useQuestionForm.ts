import { useReducer, useMemo } from 'react';

import { useToast } from '@/shared/components/Toast/ToastContext';

import { QuestionRequest } from '../../types/Event';
import { MAX_LENGTH, MAX_QUESTIONS } from '../constants/errorMessages';

type Action =
  | { type: 'ADD' }
  | { type: 'DELETE'; index: number }
  | { type: 'UPDATE'; index: number; data: Partial<QuestionRequest> }
  | { type: 'LOAD_ALL'; data: QuestionRequest[] };

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
      return state
        .filter((_, currentIndex) => currentIndex !== action.index)
        .map((question, newIndex) => ({ ...question, orderIndex: newIndex }));
    }
    case 'UPDATE': {
      return state.map((question, currentIndex) =>
        currentIndex === action.index ? { ...question, ...action.data } : question
      );
    }
    case 'LOAD_ALL': {
      return Array.isArray(action.data) ? action.data : [];
    }
    default:
      return state;
  }
};

export const useQuestionForm = () => {
  const { error } = useToast();
  const [questions, dispatch] = useReducer(reducer, []);
  const canAddQuestion = questions.length < MAX_QUESTIONS;

  const addQuestion = () => {
    if (!canAddQuestion) {
      error('질문은 최대 5개까지 추가 가능합니다.');
      return;
    }
    dispatch({ type: 'ADD' });
  };

  const deleteQuestion = (index: number) => {
    dispatch({ type: 'DELETE', index });
  };

  const updateQuestion = (index: number, data: Partial<QuestionRequest>) => {
    dispatch({ type: 'UPDATE', index, data });
  };

  const loadQuestions = (list: QuestionRequest[]) => {
    dispatch({ type: 'LOAD_ALL', data: list ?? [] });
  };

  const isValid = useMemo(() => {
    return questions.every((question) => {
      if (!question.questionText.trim()) return false;
      if (question.questionText.length > MAX_LENGTH.QUESTION) return false;
      return true;
    });
  }, [questions]);

  return {
    questions,
    addQuestion,
    deleteQuestion,
    updateQuestion,
    loadQuestions,
    isValid,
  };
};
