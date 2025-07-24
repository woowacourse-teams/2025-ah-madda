import { useMutation, useQueryClient } from '@tanstack/react-query';

import { eventMutationOptions, eventQueryKeys } from '@/api/queries/event';

export const usePostAlarm = (eventId: number) => {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: (content: string) => eventMutationOptions.alarms(eventId).mutationFn(content),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventQueryKeys.alarm() });
    },
  });
};
