import { useState } from 'react';

export type DropdownType = 'eventDateRange' | 'registrationEnd';

export const useDropdownStates = () => {
  const [openDropdown, setOpenDropdown] = useState<DropdownType | null>(null);

  const openDropdownHandler = (type: DropdownType) => {
    setOpenDropdown(type);
  };

  const closeDropdownHandler = () => {
    setOpenDropdown(null);
  };

  const isDropdownOpen = (type: DropdownType) => {
    return openDropdown === type;
  };

  return {
    openDropdown: openDropdownHandler,
    closeDropdown: closeDropdownHandler,
    isOpen: isDropdownOpen,
  };
};
