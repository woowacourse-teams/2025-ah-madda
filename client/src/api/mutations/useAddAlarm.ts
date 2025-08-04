import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';

export const postAlarm = async (eventId: number, content: string) => {
  return await fetcher.post(`events/${eventId}/notify-non-guests`, { content });
};

export const useAddAlarm = ({ eventId }: { eventId: number }) => {
  return useMutation({
    mutationFn: (content: string) => postAlarm(eventId, content),
  });
};
