export const formatKoreanDateTime = (isoString: string): string => {
  const date = new Date(isoString);

  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();

  const weekdayNames = ['일요일', '월요일', '화요일', '수요일', '목요일', '금요일', '토요일'];
  const weekday = weekdayNames[date.getDay()];

  let hour = date.getHours();
  const minute = String(date.getMinutes()).padStart(2, '0');
  const isPM = hour >= 12;
  const period = isPM ? '오후' : '오전';

  hour = hour % 12 || 12;

  return `${year}년 ${month}월 ${day}일 ${weekday} ${period} ${String(hour).padStart(2, '0')}:${minute}`;
};
