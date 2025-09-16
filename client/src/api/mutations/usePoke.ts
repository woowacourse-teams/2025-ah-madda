import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { PokeAPIRequest } from '../types/notification';

export const createPoke = async (
  eventId: number,
  receiptOrganizationMemberId: number,
  pokeMessage: string
) => {
  await fetcher.post<void>(`events/${eventId}/poke`, { receiptOrganizationMemberId, pokeMessage });
};

export const usePoke = (eventId: number) => {
  return useMutation({
    mutationFn: ({ receiptOrganizationMemberId, pokeMessage }: PokeAPIRequest) =>
      createPoke(eventId, receiptOrganizationMemberId, pokeMessage),
  });
};
