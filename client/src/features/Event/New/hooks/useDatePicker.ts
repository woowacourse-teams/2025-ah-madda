import { useState } from 'react';

type UseDatePickerProps = {
  initialStartDate?: Date | null;
  initialEndDate?: Date | null;
  initialStartTime?: Date;
  initialEndTime?: Date;
  onSelect: (startDate: Date, endDate: Date, startTime?: Date, endTime?: Date) => void;
  onClose: () => void;
};

export const useDatePicker = ({
  initialStartDate,
  initialEndDate,
  initialStartTime,
  initialEndTime,
  onSelect,
  onClose,
}: UseDatePickerProps) => {
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

  const handleReset = () => {
    setSelectedDate(initialStartDate || null);
    setSelectedEndDate(initialEndDate || null);
    setSelectedStartTime(initialStartTime);
    setSelectedEndTime(initialEndTime);
  };

  const isConfirmDisabled = !selectedDate || !selectedStartTime || !selectedEndTime;

  return {
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
  };
};
