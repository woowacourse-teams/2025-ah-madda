import { createPortal } from 'react-dom';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { IconButton } from '../IconButton';

import { StyledToastLayout, StyledToastContainer, StyledToastProgressBar } from './Toast.styled';

export type ToastVariant = 'success' | 'error';

export type ToastProps = {
  /**
   * Message text to display inside the toast.
   * @type {string}
   * @example "Operation failed."
   */
  message: string;

  /**
   * Duration (in milliseconds) before the toast automatically disappears.
   * @default 3000
   * @type {number}
   */
  duration?: number;

  /**
   * Callback when the toast is closed (either by user or timeout).
   * @type {() => void}
   */
  onClose?: () => void;

  /**
   * Visual style of the toast (e.g. success or error).
   * @default 'success'
   */
  variant?: ToastVariant;
};

export const Toast = ({ message, duration = 3000, onClose, variant = 'success' }: ToastProps) => {
  const variantColor = variant === 'success' ? theme.colors.primary600 : theme.colors.red600;

  const toastContent = (
    <StyledToastLayout>
      <StyledToastContainer>
        <Flex dir="row" width="100%" justifyContent="space-between" alignItems="center">
          <Text type="Body" color="#666">
            {message}
          </Text>

          <IconButton onClick={onClose} aria-label="close" name="close" size={16} />
        </Flex>

        <StyledToastProgressBar color={variantColor} duration={duration} />
      </StyledToastContainer>
    </StyledToastLayout>
  );

  return createPortal(toastContent, document.body);
};
