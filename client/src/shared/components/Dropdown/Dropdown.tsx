import { createContext, useContext, useState, useRef, ReactNode, RefObject } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { useClickOutside } from '@/shared/hooks/useClickOutside';

import { DropdownContentContainer } from './Dropdown.styled';

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
  const triggerRef = useRef<HTMLElement>(null);

  useClickOutside({ ref: triggerRef, isOpen, onClose: () => setIsOpen(false) });

  const contextValue: DropdownContextValue = {
    isOpen,
    setIsOpen,
    triggerRef,
  };

  return (
    <DropdownContext.Provider value={contextValue}>
      <Flex
        css={css`
          position: relative;
        `}
      >
        {children}
      </Flex>
    </DropdownContext.Provider>
  );
};

export const DropdownTrigger = ({ children }: DropdownTriggerProps) => {
  const { isOpen, setIsOpen, triggerRef } = useDropdownContext();

  const handleClick = () => {
    setIsOpen(!isOpen);
  };

  return (
    <Button
      ref={triggerRef as RefObject<HTMLButtonElement>}
      onClick={handleClick}
      variant="outline"
      size="full"
    >
      {children}
    </Button>
  );
};

export const DropdownContent = ({ children }: DropdownContentProps) => {
  const { isOpen } = useDropdownContext();

  if (!isOpen) return null;

  return <DropdownContentContainer>{children}</DropdownContentContainer>;
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
    <Button onClick={handleClick} disabled={disabled} variant="outline" size="md">
      {children}
    </Button>
  );
};

Dropdown.Trigger = DropdownTrigger;
Dropdown.Content = DropdownContent;
Dropdown.Item = DropdownItem;
