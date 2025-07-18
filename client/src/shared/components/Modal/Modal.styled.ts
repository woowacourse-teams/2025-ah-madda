import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { ModalProps } from './Modal';

export type Size = 'small' | 'medium' | 'large';

const sizeStyles = {
  small: css`
    width: 304px;
  `,
  medium: css`
    width: 40%;
  `,
  large: css`
    width: 70%;
  `,
} as const;

export const StyledModalLayout = styled.div`
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
`;

export const StyledModalContainer = styled.div<Pick<ModalProps, 'size'>>`
  background-color: #ffffff;
  min-height: 216px;
  padding: 24px 32px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
  align-items: center;
  border-radius: 8px;
  ${({ size }) => size && sizeStyles[size]};
`;

export const StyledModalWrapper = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
`;

export const StyledCloseButtonWrapper = styled.div`
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 1;
`;
