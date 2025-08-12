// Date 객체를 datetime-local input에서 사용할 수 있는 형식으로 변환
export const formatDateForInput = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}-${month}-${day}T${hours}:${minutes}`;
};

export const parseInputDate = (datetimeLocal: string): Date | null => {
  if (!datetimeLocal) return null;

  const [datePart, timePart] = datetimeLocal.split('T');
  if (!datePart || !timePart) return null;

  const [year, month, day] = datePart.split('-').map(Number);
  const [hour, minute] = timePart.split(':').map(Number);

  return new Date(year, month - 1, day, hour, minute);
};

export const setTimeToDate = (targetDate: Date, timeSource: Date): Date => {
  return new Date(
    targetDate.getFullYear(),
    targetDate.getMonth(),
    targetDate.getDate(),
    timeSource.getHours(),
    timeSource.getMinutes()
  );
};
