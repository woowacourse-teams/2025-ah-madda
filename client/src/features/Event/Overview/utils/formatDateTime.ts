export const formatDateTime = (eventStart: string, eventEnd: string): string => {
  const startDate = new Date(eventStart);
  const endDate = new Date(eventEnd);

  const pad = (n: number) => n.toString().padStart(2, '0');

  const month = startDate.getMonth() + 1;
  const day = startDate.getDate();
  const weekday = startDate.toLocaleDateString('ko-KR', { weekday: 'short' });

  const startHour = pad(startDate.getHours());
  const startMinute = pad(startDate.getMinutes());
  const endHour = pad(endDate.getHours());
  const endMinute = pad(endDate.getMinutes());

  return `${month}.${day} (${weekday}) ${startHour}:${startMinute} ~ ${endHour}:${endMinute}`;
};
