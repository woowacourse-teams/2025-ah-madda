import { ComponentProps, PropsWithChildren, useRef } from 'react';

import { createPortal } from 'react-dom';

import { useEscapeKey } from '@/shared/hooks/useEscapeKey';
import { useFocusTrap } from '@/shared/hooks/useFocusTrap';
import { useLockScroll } from '@/shared/hooks/useLockScroll';

import {
  StyledModalLayout,
  StyledModalWrapper,
  StyledModalContainer,
  StyledCloseButtonWrapper,
} from './Modal.styled';

const getModalRoot = () => {
  let root = document.getElementById('modal-root');
  if (!root) {
    root = document.createElement('div');
    root.id = 'modal-root';
    document.body.appendChild(root);
    console.warn('💡 modal-root가 없어서 동적으로 생성했습니다.');
  }
  return root;
};

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
   * Content to render inside the modal (includes header, body, footer, etc.).
   */
  children: React.ReactNode;
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
} & PropsWithChildren<ComponentProps<'div'>>;

const isStorybook = typeof window !== 'undefined' && (window as any).__STORYBOOK__;

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

  const modalRoot = document.getElementById('modal-root');
  if (!modalRoot) return null;

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

  return isStorybook ? modalContent : createPortal(modalContent, getModalRoot());
};
