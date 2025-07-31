export const formatDateTime = (
  eventStart: string | null | undefined,
  eventEnd: string | null | undefined
): string => {
  if (!eventStart || !eventEnd) {
    return '날짜 정보가 없습니다';
  }

  const startDate = new Date(eventStart);
  const endDate = new Date(eventEnd);

  if (isNaN(startDate.getTime()) || isNaN(endDate.getTime())) {
    return '잘못된 날짜 형식입니다';
  }

  const month = startDate.getMonth() + 1;
  const day = startDate.getDate();
  const weekday = startDate.toLocaleDateString('ko-KR', { weekday: 'short' });

  const startHour = startDate.getHours();
  const endHour = endDate.getHours();

  return `${month}.${day} (${weekday}) ${startHour}시 ~ ${endHour}시`;
};
