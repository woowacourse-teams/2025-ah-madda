import { useState } from 'react';

import type { TimeValue } from '../types/time';
import { compareTimeValues } from '../utils/time';

export type TimeSelectionMode = 'single' | 'range';

type SingleTimeSelectionProps = {
  mode: 'single';
  initialTime?: TimeValue;
};

type RangeTimeSelectionProps = {
  mode: 'range';
  initialStartTime?: TimeValue;
  initialEndTime?: TimeValue;
};

export type UseTimeSelectionProps = SingleTimeSelectionProps | RangeTimeSelectionProps;

export const useTimeSelection = ({ mode, ...props }: UseTimeSelectionProps) => {
  const isSingle = mode === 'single';

  const singleProps = props as Omit<SingleTimeSelectionProps, 'mode'>;
  const rangeProps = props as Omit<RangeTimeSelectionProps, 'mode'>;

  const [selectedTime, setSelectedTime] = useState<TimeValue>(
    isSingle ? singleProps.initialTime || null : null
  );

  const [selectedStartTime, setSelectedStartTime] = useState<TimeValue>(
    isSingle ? null : rangeProps.initialStartTime || null
  );

  const [selectedEndTime, setSelectedEndTime] = useState<TimeValue>(
    isSingle ? null : rangeProps.initialEndTime || null
  );

  const resetTimes = () => {
    if (isSingle) {
      setSelectedTime(null);
    } else {
      setSelectedStartTime(null);
      setSelectedEndTime(null);
    }
  };

  const restoreInitialTimes = () => {
    if (isSingle) {
      setSelectedTime(singleProps.initialTime || null);
    } else {
      setSelectedStartTime(rangeProps.initialStartTime || null);
      setSelectedEndTime(rangeProps.initialEndTime || null);
    }
  };

  const isTimeValid = (isSameDay = false) => {
    if (isSingle) {
      return !!selectedTime;
    }

    if (!selectedStartTime || !selectedEndTime) return false;

    if (isSameDay) {
      return compareTimeValues(selectedEndTime, selectedStartTime) >= 0;
    }

    return true;
  };

  const getTimeValidationError = (isSameDay = false) => {
    if (isSingle) {
      return !selectedTime ? '시간을 선택해주세요' : null;
    }

    if (!selectedStartTime) return '시작 시간을 선택해주세요';
    if (!selectedEndTime) return '종료 시간을 선택해주세요';

    if (isSameDay && compareTimeValues(selectedEndTime, selectedStartTime) < 0) {
      return '종료 시간은 시작 시간보다 늦어야 합니다';
    }

    return null;
  };

  return {
    selectedTime,
    selectedStartTime,
    selectedEndTime,
    mode,
    setSelectedTime: isSingle ? setSelectedTime : () => {},
    setSelectedStartTime: isSingle ? () => {} : setSelectedStartTime,
    setSelectedEndTime: isSingle ? () => {} : setSelectedEndTime,
    resetTimes,
    restoreInitialTimes,
    isTimeValid,
    getTimeValidationError,
  };
};
