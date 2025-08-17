import { useMutation } from '@tanstack/react-query';

import { optOutEventNotification, optInEventNotification } from '../queries/notification';

export const useEventNotificationToggle = (eventId: number) => {
  const optOut = useMutation({
    mutationFn: () => optOutEventNotification(eventId),
  });
  const optIn = useMutation({
    mutationFn: () => optInEventNotification(eventId),
  });

  return {
    optOut,
    optIn,
    isLoading: optOut.isPending || optIn.isPending,
  };
};
