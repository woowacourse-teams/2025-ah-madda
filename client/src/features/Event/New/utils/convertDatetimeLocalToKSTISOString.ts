export const convertDatetimeLocalToKSTISOString = (datetimeLocal: string): string => {
  if (!datetimeLocal) return '';

  const [datePart, timePart] = datetimeLocal.split('T');
  if (!datePart || !timePart) return '';

  const [year, month, day] = datePart.split('-').map(Number);
  const [hour, minute] = timePart.split(':').map(Number);

  const fakeUTCDate = new Date(Date.UTC(year, month - 1, day, hour, minute));

  return fakeUTCDate.toISOString();
};
