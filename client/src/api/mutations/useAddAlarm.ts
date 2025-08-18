import { useMutation, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryKeys } from '../queries/event';
import { NotificationAPIRequest } from '../types/notification';

export const postAlarm = async (eventId: number, data: NotificationAPIRequest) => {
  return await fetcher.post(`events/${eventId}/notify-organization-members`, data);
};

export const useAddAlarm = ({ eventId }: { eventId: number }) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (data: NotificationAPIRequest) => postAlarm(eventId, data),
    onSuccess: () => {
      queryClient.invalidateQueries({
        queryKey: [...eventQueryKeys.history(), eventId],
      });
    },
  });
};
