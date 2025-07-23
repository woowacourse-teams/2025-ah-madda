export const convertToISOString = (datetimeString: string): string => {
  if (!datetimeString) return '';

  const [datePart, timePart] = datetimeString.split(' ');
  if (!datePart || !timePart) return '';

  const formattedDate = datePart.replace(/\./g, '-');

  const isoCandidate = `${formattedDate}T${timePart}:00`;

  const date = new Date(isoCandidate);
  if (isNaN(date.getTime())) {
    throw new RangeError(`Invalid date format: ${datetimeString}`);
  }

  return date.toISOString();
};
