import { useState } from 'react';

type UseDatePickerProps = {
  initialStartDate?: Date | null;
  initialEndDate?: Date | null;
  initialStartTime?: Date;
  initialEndTime?: Date;
  onSelect: (startDate: Date, endDate: Date, startTime: Date, endTime: Date) => void;
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
    if (!selectedDate || !selectedStartTime || !selectedEndTime) return;
    const endDate = selectedEndDate || selectedDate;
    if (selectedEndDate && selectedEndDate.getTime() < selectedDate.getTime()) return;
    if (
      endDate.toDateString() === selectedDate.toDateString() &&
      selectedEndTime.getHours() * 60 + selectedEndTime.getMinutes() <
        selectedStartTime.getHours() * 60 + selectedStartTime.getMinutes()
    ) {
      return;
    }
    onSelect(selectedDate, endDate, selectedStartTime, selectedEndTime);
    onClose();
  };

  const handleCancel = () => {
    setSelectedDate(initialStartDate || null);
    setSelectedEndDate(initialEndDate || null);
    setSelectedStartTime(initialStartTime);
    setSelectedEndTime(initialEndTime);
    onClose();
  };

  const handleReset = () => {
    setSelectedDate(null);
    setSelectedEndDate(null);
    setSelectedStartTime(undefined);
    setSelectedEndTime(undefined);
  };

  const endDateForCheck = selectedEndDate || selectedDate;

  const isSameDay =
    !!selectedDate &&
    !!endDateForCheck &&
    selectedDate.getFullYear() === endDateForCheck.getFullYear() &&
    selectedDate.getMonth() === endDateForCheck.getMonth() &&
    selectedDate.getDate() === endDateForCheck.getDate();

  let isEndBeforeStartOnSameDay = false;
  if (isSameDay && selectedStartTime && selectedEndTime) {
    const start = new Date(selectedDate!);
    start.setHours(
      selectedStartTime.getHours(),
      selectedStartTime.getMinutes(),
      selectedStartTime.getSeconds(),
      selectedStartTime.getMilliseconds()
    );
    const end = new Date(selectedDate!);
    end.setHours(
      selectedEndTime.getHours(),
      selectedEndTime.getMinutes(),
      selectedEndTime.getSeconds(),
      selectedEndTime.getMilliseconds()
    );
    isEndBeforeStartOnSameDay = end.getTime() < start.getTime();
  }

  const isConfirmDisabled =
    !selectedDate || !selectedStartTime || !selectedEndTime || isEndBeforeStartOnSameDay;

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
