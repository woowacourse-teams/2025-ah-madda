import { RefObject } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Calendar } from '@/shared/components/Calendar';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { TimePicker } from '@/shared/components/TimePicker';
import { formatTimeDisplay } from '@/shared/components/TimePicker/utils';

import { DatePickerContainer } from '../containers/DatePickerContainer';
import { useDatePicker } from '../hooks/useDatePicker';

import type { DatePickerProps } from './DatePickerDropdown';

export type RangeDatePickerProps = DatePickerProps & {
  mode: 'range';
  onSelect: (startDate: Date, endDate: Date, startTime?: Date, endTime?: Date) => void;
  initialStartDate?: Date | null;
  initialEndDate?: Date | null;
  initialStartTime?: Date;
  initialEndTime?: Date;
  title?: string;
  dropdownRef?: RefObject<HTMLDivElement | null>;
};

export const RangeDatePicker = ({
  onSelect,
  onClose,
  initialStartDate,
  initialEndDate,
  initialStartTime,
  initialEndTime,
  title = '이벤트 날짜 및 시간 선택',
  dropdownRef,
}: RangeDatePickerProps) => {
  const {
    selectedDate,
    selectedEndDate,
    selectedStartTime,
    selectedEndTime,
    setSelectedStartTime,
    setSelectedEndTime,
    handleDateSelect,
    handleDateRangeSelect,
    handleConfirm,
    handleCancel,
    isConfirmDisabled,
  } = useDatePicker({
    initialStartDate,
    initialEndDate,
    initialStartTime,
    initialEndTime,
    onSelect,
    onClose,
  });

  return (
    <DatePickerContainer ref={dropdownRef}>
      <Text type="Heading" weight="bold" color="black">
        {title}
      </Text>
      <Flex dir="column" gap="20px" padding="20px 0 0 0">
        <Flex
          dir="row"
          css={css`
            align-items: flex-start;

            @media (max-width: 768px) {
              flex-direction: column;
              align-items: center;
            }
          `}
        >
          <Calendar
            selectedDate={selectedDate || null}
            selectedEndDate={selectedEndDate || null}
            onSelectDate={handleDateSelect}
            onSelectDateRange={handleDateRangeSelect}
            mode="range"
          />

          <Flex
            dir="column"
            css={css`
              @media (max-width: 768px) {
                width: 100%;
                align-items: center;
              }
            `}
          >
            <TimePicker
              label="시작 시간"
              selectedTime={selectedStartTime}
              onTimeChange={setSelectedStartTime}
            />

            <TimePicker
              label="종료 시간"
              selectedTime={selectedEndTime}
              onTimeChange={setSelectedEndTime}
            />
          </Flex>
        </Flex>

        <Flex dir="column" gap="8px">
          <Text type="Body" weight="medium" color="gray">
            선택된 날짜 및 시간
          </Text>

          <Flex dir="row" gap="8px">
            <Text type="Body" color="black">
              시작:
            </Text>
            <Text type="Body" color="#3993FF">
              {selectedDate ? selectedDate.toLocaleDateString('ko-KR') : '날짜 미선택'}
              {selectedStartTime &&
                ` ${formatTimeDisplay(selectedStartTime.getHours(), selectedStartTime.getMinutes())}`}
            </Text>
          </Flex>

          <Flex dir="row" gap="8px">
            <Text type="Body" color="black">
              종료:
            </Text>
            <Text type="Body" color="#3993FF">
              {selectedEndDate || selectedDate
                ? (selectedEndDate || selectedDate)!.toLocaleDateString('ko-KR')
                : '날짜 미선택'}
              {selectedEndTime &&
                ` ${formatTimeDisplay(selectedEndTime.getHours(), selectedEndTime.getMinutes())}`}
            </Text>
          </Flex>
        </Flex>

        <Flex dir="row" gap="12px" justifyContent="center">
          <Button variant="outline" onClick={handleCancel}>
            취소
          </Button>
          <Button disabled={isConfirmDisabled} onClick={handleConfirm}>
            확인
          </Button>
        </Flex>
      </Flex>
    </DatePickerContainer>
  );
};
