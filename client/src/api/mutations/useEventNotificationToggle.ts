import { useMutation, useSuspenseQuery } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { eventQueryOptions } from '../queries/event';

export const useEventNotificationToggle = (eventId: number) => {
  const { data } = useSuspenseQuery(eventQueryOptions.notificationOptOutState(eventId));

  const optOut = useMutation({
    mutationFn: () => fetcher.post<void>(`events/${eventId}/notification/opt-out`),
  });
  const optIn = useMutation({
    mutationFn: () => fetcher.delete(`events/${eventId}/notification/opt-out`),
  });

  return {
    optOut,
    optIn,
    isLoading: optOut.isPending || optIn.isPending,
    data,
  };
};
