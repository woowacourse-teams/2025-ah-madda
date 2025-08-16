import { useMutation } from '@tanstack/react-query';

import { optOutEventNotification, optInEventNotification } from '../queries/notification';

export const useEventNotificationToggle = (eventId: number) => {
  const optOut = useMutation({
    mutationFn: () => optOutEventNotification(eventId),
  });
  const OptIn = useMutation({
    mutationFn: () => optInEventNotification(eventId),
  });

  return {
    optOut,
    OptIn,
    isLoading: optOut.isPending || OptIn.isPending,
  };
};
