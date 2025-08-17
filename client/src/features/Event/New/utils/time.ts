import { type TimeValue } from '../types/time';

export const timeValueFromDate = (date: Date | null | undefined): TimeValue => {
  if (!date) return null;
  return {
    hours: date.getHours(),
    minutes: date.getMinutes(),
  };
};

export const timeValueToMinutes = (time: TimeValue): number => {
  if (!time) return 0;
  return time.hours * 60 + time.minutes;
};

export const compareTimeValues = (time1: TimeValue, time2: TimeValue): number => {
  const minutes1 = timeValueToMinutes(time1);
  const minutes2 = timeValueToMinutes(time2);
  return minutes1 - minutes2;
};

export const timeValueToDate = (time: TimeValue, baseDate?: Date): Date | null => {
  if (!time) return null;
  const date = baseDate ? new Date(baseDate) : new Date();
  date.setHours(time.hours, time.minutes, 0, 0);
  return date;
};
