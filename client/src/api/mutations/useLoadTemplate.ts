import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import type { EventTemplateAPIResponse } from '../types/event';

const getEventTemplate = async (eventId: number) => {
  return await fetcher.get<EventTemplateAPIResponse>(
    `organizations/events/${eventId}/owned/template`
  );
};

export const useLoadTemplate = () => {
  return useMutation({
    mutationFn: (eventId: number) => getEventTemplate(eventId),
  });
};
