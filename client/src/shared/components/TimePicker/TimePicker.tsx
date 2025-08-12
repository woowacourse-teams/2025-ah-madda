import { Flex } from '../Flex';
import { Text } from '../Text';

import { StyledSelect, StyledTimePicker } from './TimePicker.styled';

type TimePickerProps = {
  selectedTime?: Date;
  onTimeChange?: (time: Date) => void;
  label?: string;
  disabled?: boolean;
};

const generateHourOptions = () => {
  const hours = [];
  for (let i = 0; i < 24; i++) {
    hours.push({
      value: i,
      label: String(i).padStart(2, '0'),
    });
  }
  return hours;
};

const generateMinuteOptions = () => {
  const minutes = [];
  for (let i = 0; i < 60; i += 10) {
    minutes.push({
      value: i,
      label: String(i).padStart(2, '0'),
    });
  }
  return minutes;
};

const HOUR_OPTIONS = generateHourOptions();
const MINUTE_OPTIONS = generateMinuteOptions();

export const TimePicker = ({
  selectedTime,
  onTimeChange,
  label = '시간 선택',
  disabled = false,
}: TimePickerProps) => {
  const currentHour = selectedTime?.getHours() ?? 9;
  const currentMinute = selectedTime?.getMinutes() ?? 0;

  const handleHourChange = (hour: number) => {
    const newTime = new Date(selectedTime || new Date());
    newTime.setHours(hour);
    newTime.setMinutes(currentMinute);
    newTime.setSeconds(0);
    onTimeChange?.(newTime);
  };

  const handleMinuteChange = (minute: number) => {
    const newTime = new Date(selectedTime || new Date());
    newTime.setHours(currentHour);
    newTime.setMinutes(minute);
    newTime.setSeconds(0);
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

      <Text type="Label" color="gray">
        선택된 시간: {String(currentHour).padStart(2, '0')}:{String(currentMinute).padStart(2, '0')}
      </Text>
    </StyledTimePicker>
  );
};
