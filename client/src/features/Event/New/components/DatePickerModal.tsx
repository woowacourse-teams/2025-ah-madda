import { useState } from 'react';

import { Button } from '../../../../shared/components/Button';
import { Calendar } from '../../../../shared/components/Calendar/Calendar';
import { Flex } from '../../../../shared/components/Flex';
import { Modal } from '../../../../shared/components/Modal';
import { Text } from '../../../../shared/components/Text';

type DatePickerModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onSelect: (startDate: Date, endDate: Date) => void;
  initialStartDate?: Date | null;
  initialEndDate?: Date | null;
};

export const DatePickerModal = ({
  isOpen,
  onClose,
  onSelect,
  initialStartDate,
  initialEndDate,
}: DatePickerModalProps) => {
  const [selectedDate, setSelectedDate] = useState<Date | null>(initialStartDate || null);
  const [selectedEndDate, setSelectedEndDate] = useState<Date | null>(initialEndDate || null);

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
      onSelect(selectedDate, endDate);
      onClose();
    }
  };

  const handleCancel = () => {
    setSelectedDate(initialStartDate || null);
    setSelectedEndDate(initialEndDate || null);
    onClose();
  };

  const isConfirmDisabled = !selectedDate;

  return (
    <Modal isOpen={isOpen} onClose={handleCancel} showCloseButton={true}>
      <Text type="Heading" weight="bold" color="black">
        이벤트 날짜 선택
      </Text>
      <Flex dir="column" gap="20px" padding="20px">
        <Calendar
          selectedDate={selectedDate || null}
          selectedEndDate={selectedEndDate || null}
          onSelectDate={handleDateSelect}
          onSelectDateRange={handleDateRangeSelect}
          mode="range"
        />

        <Flex dir="column" gap="8px">
          <Text type="Body" weight="medium" color="gray">
            선택된 날짜
          </Text>

          <Flex dir="row" gap="8px">
            <Text type="Body" color="black">
              시작일:
            </Text>
            <Text type="Body" color="#3993FF">
              {selectedDate ? selectedDate.toLocaleDateString('ko-KR') : '선택되지 않음'}
            </Text>
          </Flex>

          <Flex dir="row" gap="8px">
            <Text type="Body" color="black">
              종료일:
            </Text>
            <Text type="Body" color="#3993FF">
              {selectedEndDate
                ? selectedEndDate.toLocaleDateString('ko-KR')
                : selectedDate
                  ? selectedDate.toLocaleDateString('ko-KR')
                  : '선택되지 않음'}
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
