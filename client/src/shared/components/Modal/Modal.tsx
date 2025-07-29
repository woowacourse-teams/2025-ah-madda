import { ComponentProps, useRef } from 'react';

import { createPortal } from 'react-dom';

import { useEscapeKey } from '../../hooks/useEscapeKey';
import { useFocusTrap } from '../../hooks/useFocusTrap';
import { useLockScroll } from '../../hooks/useLockScroll';

import {
  StyledModalLayout,
  StyledModalWrapper,
  StyledModalContainer,
  StyledCloseButtonWrapper,
} from './Modal.styled';

export type ModalProps = {
  /**
   * Whether the modal is open.
   */
  isOpen: boolean;
  /**
   * Called when the modal is requested to close (ESC, outside click).
   */
  onClose: () => void;
  /**
   * Modal size.
   * @default 'sm'
   */
  size?: 'sm' | 'md' | 'lg';
  /**
   * Whether clicking the backdrop should close the modal.
   * @default true
   */
  shouldCloseOnBackdropClick?: boolean;
  /**
   * Whether to show the close (X) button at the top-right corner.
   * @default true
   */
  showCloseButton?: boolean;
} & ComponentProps<'div'>;

export const Modal = ({
  isOpen,
  onClose,
  children,
  size = 'sm',
  shouldCloseOnBackdropClick = true,
  showCloseButton = true,
  ...props
}: ModalProps) => {
  const modalRef = useRef<HTMLDivElement | null>(null);

  useFocusTrap(modalRef);
  useEscapeKey(onClose);
  useLockScroll();

  if (!isOpen) return null;

  const handleBackdropClick = (e: React.MouseEvent) => {
    if (shouldCloseOnBackdropClick && e.target === e.currentTarget) {
      onClose();
    }
  };

  const modalContent = (
    <StyledModalLayout onClick={handleBackdropClick}>
      <StyledModalContainer size={size} {...props}>
        <StyledModalWrapper ref={modalRef}>
          {showCloseButton && (
            <StyledCloseButtonWrapper>
              <button onClick={onClose}>X</button>
            </StyledCloseButtonWrapper>
          )}
          {children}
        </StyledModalWrapper>
      </StyledModalContainer>
    </StyledModalLayout>
  );

  return createPortal(modalContent, document.body);
};
