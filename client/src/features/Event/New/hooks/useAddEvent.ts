import { useMutation, useQueryClient } from '@tanstack/react-query';

import { createEventAPI, eventQueryKeys } from '@/api/queries/event';
import type { CreateEventRequest } from '@/features/Event/types/Event';

export const useAddEvent = (organizationId: number) => {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: (payload: CreateEventRequest) => createEventAPI(organizationId, payload),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: eventQueryKeys.all() });
    },
  });
};
