export const isProvided = (v: unknown): boolean => {
  if (v == null) return false;
  if (typeof v === 'string') return v.trim() !== '';
  return true;
};

export const computeEverNonEmpty = <T extends Record<string, unknown>>(form: T) => {
  const map: Partial<Record<keyof T, boolean>> = {};
  (Object.keys(form) as Array<keyof T>).forEach((k) => {
    map[k] = isProvided(form[k]);
  });
  return map;
};
