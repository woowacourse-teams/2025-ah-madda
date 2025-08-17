import type { TimeValue } from '../types/time';

import { useDateSelection } from './useDateSelection';
import { useTimeSelection } from './useTimeSelection';

export type UseRangeDatePickerProps = {
  onClose: () => void;
  onSelect: (startDate: Date, endDate: Date, startTime: TimeValue, endTime: TimeValue) => void;
  initialStartDate?: Date | null;
  initialEndDate?: Date | null;
  initialStartTime?: TimeValue;
  initialEndTime?: TimeValue;
};

export const useRangeDatePicker = ({
  onClose,
  onSelect,
  initialStartDate,
  initialEndDate,
  initialStartTime,
  initialEndTime,
}: UseRangeDatePickerProps) => {
  const dateSelection = useDateSelection({
    mode: 'range',
    initialStartDate,
    initialEndDate,
  });

  const timeSelection = useTimeSelection({
    mode: 'range',
    initialStartTime,
    initialEndTime,
  });

  const handleConfirm = () => {
    const { selectedStartDate, selectedEndDate } = dateSelection;
    const { selectedStartTime, selectedEndTime } = timeSelection;

    if (!selectedStartDate || !selectedStartTime || !selectedEndTime) return;

    const endDate = selectedEndDate || selectedStartDate;
    if (selectedEndDate && selectedEndDate.getTime() < selectedStartDate.getTime()) return;

    const isSameDay = endDate.toDateString() === selectedStartDate.toDateString();
    if (!timeSelection.isTimeValid(isSameDay)) return;

    onSelect(selectedStartDate, endDate, selectedStartTime, selectedEndTime);
    onClose();
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

  const isConfirmDisabled = (() => {
    if (!dateSelection.isDateValid()) return true;

    const isSameDay =
      dateSelection.selectedStartDate && dateSelection.selectedEndDate
        ? dateSelection.selectedStartDate.toDateString() ===
          (dateSelection.selectedEndDate || dateSelection.selectedStartDate).toDateString()
        : false;

    return !timeSelection.isTimeValid(isSameDay);
  })();

  return {
    selectedStartDate: dateSelection.selectedStartDate,
    selectedEndDate: dateSelection.selectedEndDate,
    handleDateSelect: dateSelection.handleDateSelect,
    handleDateRangeSelect: dateSelection.handleDateRangeSelect,

    selectedStartTime: timeSelection.selectedStartTime,
    selectedEndTime: timeSelection.selectedEndTime,
    setSelectedStartTime: timeSelection.setSelectedStartTime,
    setSelectedEndTime: timeSelection.setSelectedEndTime,

    handleConfirm,
    handleCancel,
    handleReset,
    isConfirmDisabled,
  };
};
