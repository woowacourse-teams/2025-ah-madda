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
};

const HOUR_OPTIONS = generateHourOptions();
const MINUTE_OPTIONS = generateMinuteOptions();

export const TimePicker = ({
  selectedTime,
  onTimeChange,
  label = '시간 선택',
  disabled = false,
}: TimePickerProps) => {
  const currentHour = selectedTime?.getHours() ?? 0;
  const currentMinute = selectedTime?.getMinutes() ?? 0;

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
          value={currentHour}
          onChange={(e) => handleHourChange(Number(e.target.value))}
          disabled={disabled}
        >
          {HOUR_OPTIONS.map((hour) => (
            <option key={hour.value} value={hour.value}>
              {hour.label}
            </option>
          ))}
        </StyledSelect>

        <Text type="Body" weight="medium" color="black">
          :
        </Text>

        <StyledSelect
          value={currentMinute}
          onChange={(e) => handleMinuteChange(Number(e.target.value))}
          disabled={disabled}
        >
          {MINUTE_OPTIONS.map((minute) => (
            <option key={minute.value} value={minute.value}>
              {minute.label}
            </option>
          ))}
        </StyledSelect>
      </Flex>
    </StyledTimePicker>
  );
};
