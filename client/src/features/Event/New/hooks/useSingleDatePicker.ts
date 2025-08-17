import { type TimeValue } from '../types/time';

import { useDateSelection } from './useDateSelection';
import { useTimeSelection } from './useTimeSelection';

export type UseSingleDatePickerProps = {
  onClose: () => void;
  onSelect: (date: Date, time: TimeValue) => void;
  initialDate?: Date | null;
  initialTime?: TimeValue;
};

export const useSingleDatePicker = ({
  onClose,
  onSelect,
  initialDate,
  initialTime,
}: UseSingleDatePickerProps) => {
  const dateSelection = useDateSelection({
    mode: 'single',
    initialDate,
  });

  const timeSelection = useTimeSelection({
    mode: 'single',
    initialTime,
  });

  const handleConfirm = () => {
    if (dateSelection.selectedDate && timeSelection.selectedTime) {
      onSelect(dateSelection.selectedDate, timeSelection.selectedTime);
      onClose();
    }
  };

  const handleCancel = () => {
    dateSelection.restoreInitialDates();
    timeSelection.restoreInitialTimes();
    onClose();
  };

  const handleReset = () => {
    dateSelection.resetDates();
    timeSelection.resetTimes();
  };

  const isConfirmDisabled = !dateSelection.isDateValid() || !timeSelection.isTimeValid();

  return {
    selectedDate: dateSelection.selectedDate,
    handleDateSelect: dateSelection.handleDateSelect,

    selectedTime: timeSelection.selectedTime,
    setSelectedTime: timeSelection.setSelectedTime,

    handleConfirm,
    handleCancel,
    handleReset,
    isConfirmDisabled,
  };
};
