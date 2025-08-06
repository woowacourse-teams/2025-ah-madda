import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { NotificationAPIRequest } from '../types/notification';

export const postAlarm = async (eventId: number, data: NotificationAPIRequest) => {
  return await fetcher.post(`events/${eventId}/notify-organization-members`, data);
};

export const useAddAlarm = ({ eventId }: { eventId: number }) => {
  return useMutation({
    mutationFn: (data: NotificationAPIRequest) => postAlarm(eventId, data),
  });
};
