import { MAX_LENGTH } from '../constants/errorMessages';

export const isEmpty = (value: string) => value.trim() === '';
export const isTooLong = (value: string) => value.length > MAX_LENGTH;
export const isFutureDate = (value: string) => new Date(value) > new Date();
export const isAfterorEqual = (a: string, b: string) => new Date(a) >= new Date(b);
export const isBefore = (a: string, b: string) => new Date(a) < new Date(b);
export const isPositiveInteger = (value: string) => /^\d+$/.test(value) && parseInt(value) > 0;
