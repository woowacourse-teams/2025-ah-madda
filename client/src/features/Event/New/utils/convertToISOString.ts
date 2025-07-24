export const convertToISOString = (datetimeString: string): string => {
  if (!datetimeString) return '';

  const [datePart, timePart] = datetimeString.split(' ');
  if (!datePart || !timePart) return '';

  const formattedDate = datePart.replace(/\./g, '-');
  const [year, month, day] = formattedDate.split('-').map(Number);
  const [hour, minute] = timePart.split(':').map(Number);

  const fakeUTC = new Date(Date.UTC(year, month - 1, day, hour, minute));

  return fakeUTC.toISOString();
};
