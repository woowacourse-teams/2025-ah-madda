export const formatDateTime = (dateString: string) => {
  const date = new Date(dateString);
  const year = date.getFullYear();
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const timeString = date.toLocaleTimeString('ko-KR', {
    hour: 'numeric',
    hour12: true,
  });

  return `${year}. ${month}. ${day}. ${timeString}`;
};
