import { useState } from 'react';

type UseSingleDatePickerProps = {
  initialDate?: Date | null;
  initialTime?: Date;
  onSelect: (date: Date, time?: Date) => void;
  onClose: () => void;
};

export const useSingleDatePicker = ({
  initialDate,
  initialTime,
  onSelect,
  onClose,
}: UseSingleDatePickerProps) => {
  const [selectedDate, setSelectedDate] = useState<Date | null>(initialDate || null);
  const [selectedTime, setSelectedTime] = useState<Date | undefined>(initialTime);

  const handleDateSelect = (date: Date) => {
    setSelectedDate(date);
  };

  const handleConfirm = () => {
    if (selectedDate) {
      onSelect(selectedDate, selectedTime);
      onClose();
    }
  };

  const handleCancel = () => {
    setSelectedDate(initialDate || null);
    setSelectedTime(initialTime);
    onClose();
  };

  const isConfirmDisabled = !selectedDate || !selectedTime;

  const handleReset = () => {
    setSelectedDate(initialDate || null);
    setSelectedTime(initialTime);
  };

  return {
    selectedDate,
    selectedTime,
    setSelectedTime,
    handleDateSelect,
    handleConfirm,
    handleCancel,
    isConfirmDisabled,
    handleReset,
  };
};
