import { QuestionRequest } from '../../types/Event';

export const useQuestionManager = (
  questions: QuestionRequest[],
  setQuestions: (q: QuestionRequest[]) => void
) => {
  const addQuestion = () => {
    const newQuestion = { orderIndex: questions.length, isRequired: false, questionText: '' };
    setQuestions([...questions, newQuestion]);
  };

  const deleteQuestion = (index: number) => {
    const updated = questions
      .filter((_, idx) => idx !== index)
      .map((q, idx) => ({ ...q, orderIndex: idx }));
    setQuestions(updated);
  };

  const updateQuestion = (index: number, data: Partial<QuestionRequest>) => {
    const updated = questions.map((q, idx) => (idx === index ? { ...q, ...data } : q));
    setQuestions(updated);
  };

  return { addQuestion, deleteQuestion, updateQuestion };
};
