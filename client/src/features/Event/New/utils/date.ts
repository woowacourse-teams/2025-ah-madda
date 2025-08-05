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
