export const getMaxEventStartDate = () => {
  const now = new Date();
  const nextYear = now.getFullYear() + 1;
  return new Date(`${nextYear}-12-31T23:59`);
};

export const getMaxEventEndDate = (eventStart: string) => {
  const start = new Date(eventStart);
  start.setDate(start.getDate() + 30);
  return start;
};

export const formatDateForInput = (date: Date): string => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  const hours = String(date.getHours()).padStart(2, '0');
  const minutes = String(date.getMinutes()).padStart(2, '0');

  return `${year}-${month}-${day}T${hours}:${minutes}`;
};

export const parseInputDate = (datetimeLocal: string) => {
  if (!datetimeLocal) return null;

  const [datePart, timePart] = datetimeLocal.split('T');

  const [year, month, day] = datePart.split('-').map(Number);
  const [hour, minute] = timePart.split(':').map(Number);

  return new Date(year, month - 1, day, hour, minute);
};

export const applyTimeToDate = (targetDate: Date, timeSource: Date): Date => {
  return new Date(
    targetDate.getFullYear(),
    targetDate.getMonth(),
    targetDate.getDate(),
    timeSource.getHours(),
    timeSource.getMinutes()
  );
};
