export const isEmpty = (value: unknown) => {
  if (typeof value === 'string') return value.trim() === '';
  return value == null;
};
