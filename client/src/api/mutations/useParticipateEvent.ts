import { useMutation } from '@tanstack/react-query';

import { postEventParticipation } from '../queries/event';
import { Answer } from '../types/event';

export const useParticipateEvent = (eventId: number) => {
  return useMutation({
    mutationKey: ['participate', eventId],
    mutationFn: (answers: Answer[]) => postEventParticipation(eventId, answers),
  });
};
