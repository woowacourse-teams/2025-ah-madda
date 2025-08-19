export const formatTime = (time: string): string => {
  const date = new Date(time);
  return new Intl.DateTimeFormat('ko-KR', {
    hour: '2-digit',
    minute: '2-digit',
    hour12: false,
  }).format(date);
};
