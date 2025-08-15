import { RefObject } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Calendar } from '@/shared/components/Calendar';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { TimePicker } from '@/shared/components/TimePicker';
import { theme } from '@/shared/styles/theme';
import { formatTimeDisplay } from '@/shared/utils/timePicker';

import { DatePickerContainer } from '../containers/DatePickerContainer';
import { useSingleDatePicker } from '../hooks/useSingleDatePicker';

import type { DatePickerProps } from './DatePickerDropdown';

export type SingleDatePickerProps = DatePickerProps & {
  mode: 'single';
  onSelect: (date: Date, time?: Date) => void;
  initialDate?: Date | null;
  initialTime?: Date;
  title?: string;
};

export const SingleDatePicker = ({
  onSelect,
  onClose,
  initialDate,
  initialTime,
  title = '날짜 및 시간 선택',
  dropdownRef,
}: SingleDatePickerProps & { dropdownRef: RefObject<HTMLDivElement | null> }) => {
  const {
    selectedDate,
    selectedTime,
    setSelectedTime,
    handleDateSelect,
    handleConfirm,
    handleCancel,
    isConfirmDisabled,
  } = useSingleDatePicker({
    initialDate,
    initialTime,
    onSelect,
    onClose,
  });

  return (
    <DatePickerContainer ref={dropdownRef}>
      <Text type="Heading" weight="bold" color={theme.colors.gray900}>
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
            onSelectDate={handleDateSelect}
            mode="single"
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
            <TimePicker label="시간" selectedTime={selectedTime} onTimeChange={setSelectedTime} />
          </Flex>
        </Flex>

        <Flex dir="column" gap="8px">
          <Text type="Body" weight="medium" color={theme.colors.gray500}>
            선택된 날짜 및 시간
          </Text>

          <Flex dir="row" gap="8px">
            <Text type="Body" color={theme.colors.gray900}>
              선택:
            </Text>
            <Text type="Body" color={theme.colors.primary500}>
              {selectedDate ? selectedDate.toLocaleDateString('ko-KR') : '날짜 미선택'}
              {selectedTime &&
                ` ${formatTimeDisplay(selectedTime.getHours(), selectedTime.getMinutes())}`}
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
