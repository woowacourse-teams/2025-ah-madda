import { useState } from 'react';

import { css } from '@emotion/react';

import { Button } from '../../../../shared/components/Button';
import { Calendar } from '../../../../shared/components/Calendar/Calendar';
import { Flex } from '../../../../shared/components/Flex';
import { Modal } from '../../../../shared/components/Modal';
import { Text } from '../../../../shared/components/Text';
import { TimePicker } from '../../../../shared/components/TimePicker';

type DatePickerModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (startDate: Date, endDate: Date, startTime?: Date, endTime?: Date) => void;
  initialStartDate?: Date | null;
  initialEndDate?: Date | null;
  initialStartTime?: Date;
  initialEndTime?: Date;
};

export const DatePickerModal = ({
  isOpen,
  onClose,
  onSelect,
  initialStartDate,
  initialEndDate,
  initialStartTime,
  initialEndTime,
}: DatePickerModalProps) => {
  const [selectedDate, setSelectedDate] = useState<Date | null>(initialStartDate || null);
  const [selectedEndDate, setSelectedEndDate] = useState<Date | null>(initialEndDate || null);
  const [selectedStartTime, setSelectedStartTime] = useState<Date | undefined>(initialStartTime);
  const [selectedEndTime, setSelectedEndTime] = useState<Date | undefined>(initialEndTime);

  const handleDateSelect = (date: Date) => {
    setSelectedDate(date);
    setSelectedEndDate(null);
  };

  const handleDateRangeSelect = (startDate: Date, endDate: Date) => {
    setSelectedDate(startDate);
    setSelectedEndDate(endDate);
  };

  const handleConfirm = () => {
    if (selectedDate) {
      const endDate = selectedEndDate || selectedDate;
      onSelect(selectedDate, endDate, selectedStartTime, selectedEndTime);
      onClose();
    }
  };

  const handleCancel = () => {
    setSelectedDate(initialStartDate || null);
    setSelectedEndDate(initialEndDate || null);
    setSelectedStartTime(initialStartTime);
    setSelectedEndTime(initialEndTime);
    onClose();
  };

  const isConfirmDisabled = !selectedDate;

  return (
    <Modal
      isOpen={isOpen}
      onClose={handleCancel}
      showCloseButton={true}
      css={css`
        max-height: 80vh;
        overflow-y: auto;
      `}
    >
      <Text type="Heading" weight="bold" color="black">
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
                ` ${String(selectedStartTime.getHours()).padStart(2, '0')}:${String(selectedStartTime.getMinutes()).padStart(2, '0')}`}
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
                ` ${String(selectedEndTime.getHours()).padStart(2, '0')}:${String(selectedEndTime.getMinutes()).padStart(2, '0')}`}
            </Text>
          </Flex>
        </Flex>

        <Flex dir="row" gap="12px" justifyContent="flex-end">
          <Button variant="outline" onClick={handleCancel}>
            취소
          </Button>
          <Button disabled={isConfirmDisabled} onClick={handleConfirm}>
            확인
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
