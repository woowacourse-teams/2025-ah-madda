import { Event } from '@/api/types/event';

type GroupEvent = {
  label: string;
  date: string;
  events: Event[];
};

// S.TODO : 추후 날짜, sort 관련 로직을 utils로 분리
export const groupEventsByDate = (events: Event[]) => {
  const today = new Date();
  const tomorrow = new Date(today);
  tomorrow.setDate(tomorrow.getDate() + 1);
  const groups = new Map<string, GroupEvent>();

  events.forEach((event) => {
    const eventDate = new Date(event.registrationEnd);
    const label = getLabel(eventDate, today, tomorrow);
    const key = eventDate.toDateString();

    if (!groups.has(key)) {
      groups.set(key, {
        label,
        date: key,
        events: [],
      });
    }

    groups.get(key)!.events.push(event);
  });

  return [...groups.values()].sort(
    (a, b) => new Date(a.date).getTime() - new Date(b.date).getTime()
  );
};

const getLabel = (eventDate: Date, today: Date, tomorrow: Date): string => {
  if (eventDate.toDateString() === today.toDateString()) {
    return '오늘';
  }
  if (eventDate.toDateString() === tomorrow.toDateString()) {
    return '내일';
  }

  return eventDate.toLocaleDateString();
};
