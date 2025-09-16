import { POKE_MESSAGES } from '@/features/Event/Detail/components/guest/PokeModal';

export type NotificationAPIRequest = {
  organizationMemberIds: number[];
  content: string;
};

export type PokeAPIRequest = {
  receiptOrganizationMemberId: number;
  pokeMessage: POKE_MESSAGES_TYPE;
};

export type POKE_MESSAGES_TYPE = keyof typeof POKE_MESSAGES;
