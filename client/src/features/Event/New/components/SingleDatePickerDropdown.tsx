import { useRef, useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Button } from '@/shared/components/Button';
import { Calendar } from '@/shared/components/Calendar/Calendar';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { TimePicker } from '@/shared/components/TimePicker';
import { formatTimeDisplay } from '@/shared/components/TimePicker/utils';
import { useClickOutside } from '@/shared/hooks/useClickOutside';

import { useSingleDatePicker } from '../hooks/useSingleDatePicker';

type SingleDatePickerDropdownProps = {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (date: Date, time?: Date) => void;
  initialDate?: Date | null;
  initialTime?: Date;
  maxDate?: Date | null;
};

export const SingleDatePickerDropdown = ({
  isOpen,
  onClose,
  onSelect,
  initialDate,
  initialTime,
}: SingleDatePickerDropdownProps) => {
  const dropdownRef = useRef<HTMLDivElement>(null);

  useClickOutside({ ref: dropdownRef, isOpen, onClose });

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

  if (!isOpen) return null;

  return (
    <StyledSingleDatePickerDropdown ref={dropdownRef}>
      <Text type="Heading" weight="bold" color="black">
        신청 종료일 및 시간 선택
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
              label="신청 종료 시간"
              selectedTime={selectedTime}
              onTimeChange={setSelectedTime}
            />
          </Flex>
        </Flex>

        <Flex dir="column" gap="8px">
          <Text type="Body" weight="medium" color="gray">
            선택된 날짜 및 시간
          </Text>

          <Flex dir="row" gap="8px">
            <Text type="Body" color="black">
              신청 종료:
            </Text>
            <Text type="Body" color="#3993FF">
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
    </StyledSingleDatePickerDropdown>
  );
};

const StyledSingleDatePickerDropdown = styled.div`
  position: absolute;
  top: 100%;
  left: 0;
  right: 0;
  background: white;
  border: 1px solid ${({ theme }) => theme.colors.gray200};
  border-radius: 8px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  z-index: 1000;
  padding: 20px;
  margin-top: 4px;
  overflow-y: auto;
`;
