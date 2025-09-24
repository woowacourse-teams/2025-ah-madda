import { useMutation } from '@tanstack/react-query';

import { fetcher } from '../fetcher';
import { POKE_MESSAGES_TYPE, PokeAPIRequest } from '../types/notification';

export const createPoke = async (
  eventId: number,
  receiptOrganizationMemberId: number,
  pokeMessage: POKE_MESSAGES_TYPE
) => {
  await fetcher.post<void>(`events/${eventId}/poke`, { receiptOrganizationMemberId, pokeMessage });
};

export const usePoke = (eventId: number) => {
  return useMutation({
    mutationFn: ({ receiptOrganizationMemberId, pokeMessage }: PokeAPIRequest) =>
      createPoke(eventId, receiptOrganizationMemberId, pokeMessage),
  });
};
