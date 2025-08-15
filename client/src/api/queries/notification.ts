import { fetcher } from '../fetcher';

export const optOutEventNotification = (eventId: number): Promise<void> =>
  fetcher.post(`events/${eventId}/notification/opt-out`);

export const optInEventNotification = (eventId: number): Promise<void> =>
  fetcher.delete(`events/${eventId}/notification/opt-out`);
