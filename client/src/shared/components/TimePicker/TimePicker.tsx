import {
  createTimeFromHour,
  createTimeFromMinute,
  generateHourOptions,
  generateMinuteOptions,
} from '@/shared/utils/timePicker';

import { Flex } from '../Flex';
import { Text } from '../Text';

import { StyledSelect, StyledTimePicker } from './TimePicker.styled';

type TimePickerProps = {
  selectedTime?: Date;
  onTimeChange?: (time: Date) => void;
  label?: string;
  disabled?: boolean;
  minTime?: Date;
  selectedDate?: Date;
};

const HOUR_OPTIONS = generateHourOptions();
const MINUTE_OPTIONS = generateMinuteOptions();

export const TimePicker = ({
  selectedTime,
  onTimeChange,
  label = '시간 선택',
  disabled = false,
  minTime,
  selectedDate,
}: TimePickerProps) => {
  const currentHour = selectedTime?.getHours() ?? 0;
  const currentMinute = selectedTime?.getMinutes() ?? 0;

  const hasTime = selectedTime !== undefined;

  const isSameDay = (() => {
    if (!selectedDate || !minTime) return false;

    const isSameYear = selectedDate.getFullYear() === minTime.getFullYear();
    const isSameMonth = selectedDate.getMonth() === minTime.getMonth();
    const isSameDate = selectedDate.getDate() === minTime.getDate();

    return isSameYear && isSameMonth && isSameDate;
  })();

  const isHourDisabled = (hour: number) => {
    if (!isSameDay || !minTime) return false;

    return hour > minTime.getHours();
  };

  const isMinuteDisabled = (minute: number) => {
    if (!isSameDay || !minTime) return false;
    return currentHour === minTime.getHours() && minute > minTime.getMinutes();
  };

  const handleHourChange = (hour: number) => {
    const newTime = createTimeFromHour(selectedTime, hour, currentMinute);
    onTimeChange?.(newTime);
  };

  const handleMinuteChange = (minute: number) => {
    const newTime = createTimeFromMinute(selectedTime, currentHour, minute);
    onTimeChange?.(newTime);
  };

  return (
    <StyledTimePicker>
      {label && (
        <Text type="Body" weight="medium" color="black">
          {label}
        </Text>
      )}

      <Flex dir="row" gap="8px" alignItems="center">
        <StyledSelect
          aria-label="시 선택"
          value={hasTime ? currentHour : ''}
          onChange={(e) => handleHourChange(Number(e.target.value))}
          disabled={disabled}
        >
          {!hasTime && (
            <option value="" disabled>
              시
            </option>
          )}
          {HOUR_OPTIONS.map((hour) => (
            <option key={hour.value} value={hour.value} disabled={isHourDisabled(hour.value)}>
              {hour.label}
            </option>
          ))}
        </StyledSelect>

        <Text type="Body" weight="medium" color="black">
          :
        </Text>

        <StyledSelect
          aria-label="분 선택"
          value={hasTime ? currentMinute : ''}
          onChange={(e) => handleMinuteChange(Number(e.target.value))}
          disabled={disabled}
        >
          {!hasTime && (
            <option value="" disabled>
              분
            </option>
          )}
          {MINUTE_OPTIONS.map((minute) => (
            <option
              key={minute.value}
              value={minute.value}
              disabled={isMinuteDisabled(minute.value)}
            >
              {minute.label}
            </option>
          ))}
        </StyledSelect>
      </Flex>
    </StyledTimePicker>
  );
};
