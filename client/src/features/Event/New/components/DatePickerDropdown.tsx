import { useRef } from 'react';

import { useClickOutside } from '@/shared/hooks/useClickOutside';

import { RangeDatePicker, type RangeDatePickerProps } from './RangeDatePicker';
import { SingleDatePicker, type SingleDatePickerProps } from './SingleDatePicker';

export type DatePickerProps = {
  isOpen: boolean;
  onClose: () => void;
};

export type DatePickerOnSelect =
  | ((date: Date, time: Date) => void)
  | ((startDate: Date, endDate: Date, startTime: Date, endTime: Date) => void);

type DatePickerDropdownProps = RangeDatePickerProps | SingleDatePickerProps;

export const DatePickerDropdown = ({
  isOpen,
  onClose,
  mode,
  onSelect,
  ...restProps
}: DatePickerDropdownProps) => {
  const dropdownRef = useRef<HTMLDivElement>(null);

  useClickOutside({ ref: dropdownRef, isOpen, onClose });

  if (!isOpen) return null;

  if (mode === 'range') {
    return (
      <RangeDatePicker
        isOpen={isOpen}
        onClose={onClose}
        mode={mode}
        onSelect={onSelect as RangeDatePickerProps['onSelect']}
        dropdownRef={dropdownRef}
        {...(restProps as Omit<RangeDatePickerProps, 'isOpen' | 'onClose' | 'mode' | 'onSelect'>)}
      />
    );
  }

  return (
    <SingleDatePicker
      isOpen={isOpen}
      onClose={onClose}
      mode={mode}
      onSelect={onSelect as SingleDatePickerProps['onSelect']}
      dropdownRef={dropdownRef}
      {...(restProps as Omit<SingleDatePickerProps, 'isOpen' | 'onClose' | 'mode' | 'onSelect'>)}
    />
  );
};
