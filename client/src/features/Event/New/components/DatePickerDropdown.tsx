import { useRef } from 'react';

import { useClickOutside } from '@/shared/hooks/useClickOutside';

import { RangeDatePicker, type RangeDatePickerProps } from './RangeDatePicker';
import { SingleDatePicker, type SingleDatePickerProps } from './SingleDatePicker';

export type DatePickerProps = {
  isOpen: boolean;
  onClose: () => void;
};

type DatePickerDropdownProps = RangeDatePickerProps | SingleDatePickerProps;

export const DatePickerDropdown = (props: DatePickerDropdownProps) => {
  const { isOpen, onClose, mode } = props;
  const dropdownRef = useRef<HTMLDivElement>(null);

  useClickOutside({ ref: dropdownRef, isOpen, onClose });

  if (!isOpen) return null;

  if (mode === 'range') {
    return <RangeDatePicker {...props} dropdownRef={dropdownRef} />;
  }

  return <SingleDatePicker {...props} dropdownRef={dropdownRef} />;
};
