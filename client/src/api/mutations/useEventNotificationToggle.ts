import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';

export const useEventNotificationToggle = (eventId: number) => {
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
  };
};
