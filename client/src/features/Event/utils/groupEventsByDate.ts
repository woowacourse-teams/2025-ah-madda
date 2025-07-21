import { Event } from '../types/Event';

export const groupEventsByDate = (events: Event[]) => {
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);

  const groups: Record<string, Event[]> = {
    오늘: [],
    내일: [],
  };

  events.forEach((event) => {
    const eventDate = new Date(event.registrationEndDate);

    if (eventDate.toDateString() === today.toDateString()) {
      groups['오늘'].push(event);
      return;
    }
    if (eventDate.toDateString() === tomorrow.toDateString()) {
      groups['내일'].push(event);
      return;
    }

    const dateKey = eventDate.toLocaleDateString('ko-KR', {
      month: 'long',
      day: 'numeric',
    });

    if (!groups[dateKey]) {
      groups[dateKey] = [];
    }

    groups[dateKey].push(event);
  });

  return groups;
};
