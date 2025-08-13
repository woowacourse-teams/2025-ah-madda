export type TimeOption = {
  value: number;
  label: string;
};

export const generateHourOptions = (): TimeOption[] => {
  const hours = [];
  for (let i = 0; i < 24; i++) {
    hours.push({
      value: i,
      label: String(i).padStart(2, '0'),
    });
  }
  return hours;
};

export const generateMinuteOptions = (): TimeOption[] => {
  const minutes = [];
  for (let i = 0; i < 60; i += 10) {
    minutes.push({
      value: i,
      label: String(i).padStart(2, '0'),
    });
  }
  return minutes;
};

export const createTimeFromHour = (
  selectedTime: Date | undefined,
  hour: number,
  currentMinute: number
): Date => {
  const newTime = new Date(selectedTime || new Date());
  newTime.setHours(hour);
  newTime.setMinutes(currentMinute);
  newTime.setSeconds(0);
  newTime.setMilliseconds(0);
  return newTime;
};

export const createTimeFromMinute = (
  selectedTime: Date | undefined,
  currentHour: number,
  minute: number
): Date => {
  const newTime = new Date(selectedTime || new Date());
  newTime.setHours(currentHour);
  newTime.setMinutes(minute);
  newTime.setSeconds(0);
  newTime.setMilliseconds(0);
  return newTime;
};

export const formatTimeDisplay = (hour: number, minute: number): string => {
  return `${String(hour).padStart(2, '0')}:${String(minute).padStart(2, '0')}`;
};
