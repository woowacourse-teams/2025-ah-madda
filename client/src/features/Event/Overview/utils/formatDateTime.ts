export const formatDateTime = (eventStart: string, eventEnd: string): string => {
  const startDate = new Date(eventStart);
  const endDate = new Date(eventEnd);

  const month = startDate.getMonth() + 1;
  const day = startDate.getDate();
  const weekday = startDate.toLocaleDateString('ko-KR', { weekday: 'short' });

  const startHour = startDate.getHours();
  const endHour = endDate.getHours();

  return `${month}.${day} (${weekday}) ${startHour}시 ~ ${endHour}시`;
};
