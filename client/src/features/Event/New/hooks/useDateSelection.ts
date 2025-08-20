import { useState } from 'react';

export type DateSelectionMode = 'single' | 'range';

type SingleDateSelectionProps = {
  mode: 'single';
  initialDate?: Date | null;
};

type RangeDateSelectionProps = {
  mode: 'range';
  initialStartDate?: Date | null;
  initialEndDate?: Date | null;
};

export type UseDateSelectionProps = SingleDateSelectionProps | RangeDateSelectionProps;

export const useDateSelection = ({ mode, ...props }: UseDateSelectionProps) => {
  const isSingle = mode === 'single';

  const singleProps = props as Omit<SingleDateSelectionProps, 'mode'>;
  const rangeProps = props as Omit<RangeDateSelectionProps, 'mode'>;

  const [selectedStartDate, setSelectedStartDate] = useState<Date | null>(
    isSingle ? singleProps.initialDate || null : rangeProps.initialStartDate || null
  );

  const [selectedEndDate, setSelectedEndDate] = useState<Date | null>(
    isSingle ? null : rangeProps.initialEndDate || null
  );

  const handleDateSelect = (date: Date) => {
    setSelectedStartDate(date);
    if (!isSingle) {
      setSelectedEndDate(null);
    }
  };

  const handleDateRangeSelect = isSingle
    ? undefined
    : (startDate: Date, endDate: Date) => {
        setSelectedStartDate(startDate);
        setSelectedEndDate(endDate);
      };

  const resetDates = () => {
    setSelectedStartDate(null);
    if (!isSingle) {
      setSelectedEndDate(null);
    }
  };

  const restoreInitialDates = () => {
    if (isSingle) {
      setSelectedStartDate(singleProps.initialDate || null);
    } else {
      setSelectedStartDate(rangeProps.initialStartDate || null);
      setSelectedEndDate(rangeProps.initialEndDate || null);
    }
  };

  const isDateValid = () => {
    if (isSingle) {
      return !!selectedStartDate;
    }

    if (!selectedStartDate) return false;
    return selectedEndDate ? selectedEndDate.getTime() >= selectedStartDate.getTime() : true;
  };

  return {
    selectedStartDate,
    selectedEndDate,
    mode,
    handleDateSelect,
    handleDateRangeSelect,
    resetDates,
    restoreInitialDates,
    isDateValid,
  };
};
