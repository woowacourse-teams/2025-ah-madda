import { useMutation, useSuspenseQuery, useQueryClient } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryOptions, NotificationOptOutState } from '../queries/event';

export const useEventNotificationToggle = (eventId: number) => {
  const queryClient = useQueryClient();
  const options = eventQueryOptions.notificationOptOutState(eventId);
  const { data } = useSuspenseQuery(options);

  const optOut = useMutation({
    mutationFn: () => fetcher.post<void>(`events/${eventId}/notification/opt-out`),
    onSuccess: () => {
      queryClient.setQueryData<NotificationOptOutState>(options.queryKey, { optedOut: true });
    },
  });

  const optIn = useMutation({
    mutationFn: () => fetcher.delete(`events/${eventId}/notification/opt-out`),
    onSuccess: () => {
      queryClient.setQueryData<NotificationOptOutState>(options.queryKey, { optedOut: false });
    },
  });

  return {
    optOut,
    optIn,
    isLoading: optOut.isPending || optIn.isPending,
    data,
  };
};
