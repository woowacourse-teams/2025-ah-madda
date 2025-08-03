import { ERROR_MESSAGES, MAX_LENGTH } from '../constants/errorMessages';

export const getQuestionErrorMessage = (text: string): string => {
  if (!text.trim()) return ERROR_MESSAGES.REQUIRED('질문');
  if (text.length > MAX_LENGTH.QUESTION)
    return ERROR_MESSAGES.MAX_LENGTH('질문', MAX_LENGTH.QUESTION);
  return '';
};
