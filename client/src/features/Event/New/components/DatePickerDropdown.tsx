import { RefObject, useRef } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Calendar } from '@/shared/components/Calendar';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { TimePicker } from '@/shared/components/TimePicker';
import { useClickOutside } from '@/shared/hooks/useClickOutside';
import { theme } from '@/shared/styles/theme';

import { DatePickerContainer } from '../containers/DatePickerContainer';
import { useRangeDatePicker, type UseRangeDatePickerProps } from '../hooks/useRangeDatePicker';
import { useSingleDatePicker, type UseSingleDatePickerProps } from '../hooks/useSingleDatePicker';
import { timeValueToDate } from '../utils/time';

type DatePickerProps = {
  isOpen: boolean;
  onClose: () => void;
  title?: string;
};

type SingleDatePickerDropdownProps = DatePickerProps & {
  mode: 'single';
} & Omit<UseSingleDatePickerProps, 'onClose'>;

type RangeDatePickerDropdownProps = DatePickerProps & {
  mode: 'range';
} & Omit<UseRangeDatePickerProps, 'onClose'>;

type DatePickerDropdownProps = SingleDatePickerDropdownProps | RangeDatePickerDropdownProps;

export const DatePickerDropdown = ({
  isOpen,
  mode,
  onClose,
  ...props
}: DatePickerDropdownProps) => {
  const dropdownRef = useRef<HTMLDivElement>(null);

  useClickOutside({ ref: dropdownRef, isOpen, onClose });

  if (!isOpen) return null;

  if (mode === 'single') {
    const singleProps = props as SingleDatePickerDropdownProps;
    return (
      <SingleDatePickerContent
        mode={mode}
        isOpen={isOpen}
        onClose={onClose}
        title={singleProps.title}
        onSelect={singleProps.onSelect}
        initialDate={singleProps.initialDate}
        initialTime={singleProps.initialTime}
        dropdownRef={dropdownRef}
      />
    );
  }

  const rangeProps = props as RangeDatePickerDropdownProps;
  return (
    <RangeDatePickerContent
      mode={mode}
      isOpen={isOpen}
      onClose={onClose}
      title={rangeProps.title}
      onSelect={rangeProps.onSelect}
      initialStartDate={rangeProps.initialStartDate}
      initialEndDate={rangeProps.initialEndDate}
      initialStartTime={rangeProps.initialStartTime}
      initialEndTime={rangeProps.initialEndTime}
      dropdownRef={dropdownRef}
    />
  );
};

const SingleDatePickerContent = ({
  onClose,
  onSelect,
  initialDate,
  initialTime,
  dropdownRef,
}: SingleDatePickerDropdownProps & { dropdownRef: RefObject<HTMLDivElement | null> }) => {
  const {
    selectedDate,
    selectedTime,
    setSelectedTime,
    handleDateSelect,
    handleConfirm,
    handleCancel,
    isConfirmDisabled,
    handleReset,
  } = useSingleDatePicker({
    onClose,
    onSelect,
    initialDate,
    initialTime,
  });

  return (
    <DatePickerContainer ref={dropdownRef}>
      <Text type="Heading" weight="bold" color={theme.colors.gray900}>
        이벤트 날짜 및 시간 선택
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
            <TimePicker
              label="시간"
              selectedTime={timeValueToDate(selectedTime) || undefined}
              onTimeChange={(date) =>
                setSelectedTime(
                  date ? { hours: date.getHours(), minutes: date.getMinutes() } : null
                )
              }
            />
          </Flex>
        </Flex>

        <Flex dir="column" gap="8px">
          <Flex dir="row" justifyContent="space-between" gap="8px">
            <Button variant="outline" size="sm" onClick={handleReset}>
              초기화
            </Button>
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

const RangeDatePickerContent = ({
  onClose,
  onSelect,
  initialStartDate,
  initialEndDate,
  initialStartTime,
  initialEndTime,
  dropdownRef,
}: RangeDatePickerDropdownProps & { dropdownRef: RefObject<HTMLDivElement | null> }) => {
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
    handleReset,
  } = useRangeDatePicker({
    onClose,
    onSelect,
    initialStartDate,
    initialEndDate,
    initialStartTime,
    initialEndTime,
  });

  return (
    <DatePickerContainer ref={dropdownRef}>
      <Text type="Heading" weight="bold" color={theme.colors.gray900}>
        신청 날짜 및 시간 선택
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
              selectedTime={timeValueToDate(selectedStartTime) || undefined}
              onTimeChange={(date) =>
                setSelectedStartTime(
                  date ? { hours: date.getHours(), minutes: date.getMinutes() } : null
                )
              }
            />
            <TimePicker
              label="종료 시간"
              selectedTime={timeValueToDate(selectedEndTime) || undefined}
              onTimeChange={(date) =>
                setSelectedEndTime(
                  date ? { hours: date.getHours(), minutes: date.getMinutes() } : null
                )
              }
            />
          </Flex>
        </Flex>

        <Flex dir="column" gap="8px">
          <Flex dir="row" justifyContent="space-between" gap="8px">
            <Button variant="outline" size="sm" onClick={handleReset}>
              초기화
            </Button>
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
