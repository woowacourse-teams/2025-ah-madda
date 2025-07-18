import { ComponentProps, PropsWithChildren, useEffect, useRef } from 'react';

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
  hasBackdropClick?: boolean;
  /**
   * Whether to show the close (X) button at the top-right corner.
   * @default true
   */
  hasCloseButton?: boolean;
} & PropsWithChildren<ComponentProps<'div'>>;

export const Modal = ({
  isOpen,
  onClose,
  children,
  size = 'sm',
  hasBackdropClick = true,
  hasCloseButton = true,
  ...props
}: ModalProps) => {
  const modalRef = useRef<HTMLDivElement | null>(null);

  useEffect(() => {
    if (!isOpen) return;

    const modal = modalRef.current;
    if (!modal) return;

    const isInIframe = window.self !== window.top;
    const originalOverflow = document.body.style.overflow;

    if (!isInIframe) {
      document.body.style.overflow = 'hidden';
    }

    const focusable = modal.querySelectorAll<HTMLElement>(
      'button, a[href], input, textarea, select, [tabindex]:not([tabindex="-1"])'
    );
    const first = focusable[0];
    const last = focusable[focusable.length - 1];
    first?.focus();

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        e.preventDefault();
        onClose();
      }
      if (e.key === 'Tab' && first && last) {
        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    };

    modal.addEventListener('keydown', handleKeyDown);
    return () => {
      modal.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = originalOverflow;
    };
  }, [isOpen, onClose]);

  if (!isOpen) return null;

  return (
    <StyledModalLayout onClick={hasBackdropClick ? onClose : undefined}>
      <StyledModalContainer size={size} onClick={(e) => e.stopPropagation()} {...props}>
        <StyledModalWrapper ref={modalRef}>
          {hasCloseButton && (
            <StyledCloseButtonWrapper>
              <button onClick={onClose}>X</button>
            </StyledCloseButtonWrapper>
          )}
          {children}
        </StyledModalWrapper>
      </StyledModalContainer>
    </StyledModalLayout>
  );
};
