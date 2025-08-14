import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { PokeAPIRequest } from '../types/notification';

export const createPoke = async (eventId: number, receiptOrganizationMemberId: number) => {
  await fetcher.post<void>(`events/${eventId}/poke`, { receiptOrganizationMemberId });
};

export const usePoke = (eventId: number) => {
  return useMutation({
    mutationFn: ({ receiptOrganizationMemberId }: PokeAPIRequest) =>
      createPoke(eventId, receiptOrganizationMemberId),
  });
};
