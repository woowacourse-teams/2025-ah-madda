import { Guest, NonGuest } from '@/features/Event/Manage/types';

import { fetcher } from '../fetcher';

export const guestManageQueryKeys = {
  all: () => ['guestManage'],
  guests: (eventId: number) => [...guestManageQueryKeys.all(), 'guests', eventId],
  nonGuests: (eventId: number) => [...guestManageQueryKeys.all(), 'nonGuests', eventId],
};

export const guestManageQueryOptions = {
  guests: (eventId: number) => ({
    queryKey: guestManageQueryKeys.guests(eventId),
    queryFn: () => getGuests(eventId),
  }),
  nonGuests: (eventId: number) => ({
    queryKey: guestManageQueryKeys.nonGuests(eventId),
    queryFn: () => getNonGuests(eventId),
  }),
};

const getGuests = async (eventId: number) => {
  const response = await fetcher.get<Guest[]>(`events/${eventId}/guests`);
  return response;
};

const getNonGuests = async (eventId: number) => {
  const response = await fetcher.get<NonGuest[]>(`events/${eventId}/non-guests`);
  return response;
};
