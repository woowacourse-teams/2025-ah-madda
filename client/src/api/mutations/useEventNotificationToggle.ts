import { useMutation } from '@tanstack/react-query';

import { optOutEventNotification, optInEventNotification } from '../queries/notification';

export const useEventNotificationToggle = (eventId: number) => {
  const optOut = useMutation({
    mutationFn: () => optOutEventNotification(eventId),
  });
  const undoOptOut = useMutation({
    mutationFn: () => optInEventNotification(eventId),
  });

  return {
    optOut,
    undoOptOut,
    isLoading: optOut.isPending || undoOptOut.isPending,
  };
};
