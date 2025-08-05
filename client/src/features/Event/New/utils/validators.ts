export const isEmpty = (value: string) => value.trim() === '';
export const isFutureDate = (value: string) => new Date(value) > new Date();
export const isAfter = (a: string, b: string) => new Date(a) > new Date(b);
export const isBeforeorEqual = (a: string, b: string) => new Date(a) <= new Date(b);
export const isPositiveInteger = (value: string) => /^\d+$/.test(value) && parseInt(value) > 0;
