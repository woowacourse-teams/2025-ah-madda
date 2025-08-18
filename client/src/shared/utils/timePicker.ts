export type TimeOption = {
  value: number;
  label: string;
};

export const HOUR_OPTIONS: ReadonlyArray<TimeOption> = Object.freeze(
  Array.from({ length: 24 }, (_, i) => ({ value: i, label: String(i).padStart(2, '0') }))
);

export const MINUTE_OPTIONS_10: ReadonlyArray<TimeOption> = Object.freeze(
  Array.from({ length: 6 }, (_, i) => {
    const minute = i * 10;
    return { value: minute, label: String(minute).padStart(2, '0') };
  })
);

export const generateHourOptions = (): TimeOption[] => HOUR_OPTIONS.slice();

export const generateMinuteOptions = (step = 10): TimeOption[] => {
  if (step === 10) {
    return MINUTE_OPTIONS_10.slice();
  }

  const minutes = [];
  for (let i = 0; i < 60; i += step) {
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
  const fixedHour = Math.max(0, Math.min(23, hour));
  const fixedMinute = Math.max(0, Math.min(59, minute));
  return `${String(fixedHour).padStart(2, '0')}:${String(fixedMinute).padStart(2, '0')}`;
};
