import { createContext, useContext, useState, useRef, ReactNode, RefObject } from 'react';

import { Button } from '@/shared/components/Button';
import { useClickOutside } from '@/shared/hooks/useClickOutside';

import { StyledDropdownContainer, StyledContentContainer } from './Dropdown.styled';

type DropdownContextValue = {
  isOpen: boolean;
  setIsOpen: (open: boolean) => void;
  triggerRef: React.RefObject<HTMLElement | null>;
};

type DropdownProps = {
  children: ReactNode;
};

type DropdownTriggerProps = {
  children: ReactNode;
};

type DropdownContentProps = {
  children: ReactNode;
};

type DropdownItemProps = {
  children: ReactNode;
  disabled?: boolean;
  onClick?: () => void;
};

const DropdownContext = createContext<DropdownContextValue | null>(null);

const useDropdownContext = () => {
  const context = useContext(DropdownContext);
  if (!context) {
    throw new Error('Dropdown 컴포넌트는 Dropdown provider 내부에 위치해야 합니다.');
  }
  return context;
};

export const Dropdown = ({ children }: DropdownProps) => {
  const [isOpen, setIsOpen] = useState(false);
  const dropdownRef = useRef<HTMLElement>(null);

  useClickOutside({ ref: dropdownRef, isOpen, onClose: () => setIsOpen(false) });

  const contextValue: DropdownContextValue = {
    isOpen,
    setIsOpen,
    triggerRef: dropdownRef,
  };

  return (
    <DropdownContext.Provider value={contextValue}>
      <StyledDropdownContainer ref={dropdownRef as RefObject<HTMLDivElement>}>
        {children}
      </StyledDropdownContainer>
    </DropdownContext.Provider>
  );
};

export const DropdownTrigger = ({ children }: DropdownTriggerProps) => {
  const { isOpen, setIsOpen } = useDropdownContext();

  const handleClick = () => {
    setIsOpen(!isOpen);
  };

  return (
    <Button onClick={handleClick} variant="outline" size="full">
      {children}
    </Button>
  );
};

export const DropdownContent = ({ children }: DropdownContentProps) => {
  const { isOpen } = useDropdownContext();

  if (!isOpen) return null;

  return <StyledContentContainer>{children}</StyledContentContainer>;
};

export const DropdownItem = ({ children, disabled, onClick }: DropdownItemProps) => {
  const { setIsOpen } = useDropdownContext();

  const handleClick = () => {
    if (!disabled) {
      onClick?.();
      setIsOpen(false);
    }
  };

  return (
    <Button onClick={handleClick} disabled={disabled} variant="outline" size="full">
      {children}
    </Button>
  );
};

Dropdown.Trigger = DropdownTrigger;
Dropdown.Content = DropdownContent;
Dropdown.Item = DropdownItem;
