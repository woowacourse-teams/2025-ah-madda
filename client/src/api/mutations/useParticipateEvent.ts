import { useMutation } from '@tanstack/react-query';

import { postEventParticipation } from '../queries/event';

export type Answer = {
  questionId: number;
  answerText: string;
};

export const useParticipateEvent = (eventId: number) => {
  return useMutation({
    mutationKey: ['participate', eventId],
    mutationFn: (answers: Answer[]) => postEventParticipation(eventId, answers),
  });
};
