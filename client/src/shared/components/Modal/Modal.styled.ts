import { css } from '@emotion/react';
import styled from '@emotion/styled';

export type Position = 'center' | 'top' | 'bottom';
export type Size = 'small' | 'medium' | 'large';

export const positionStyles = {
  center: css`
    align-items: center;
    border-radius: 8px;
  `,
  top: css`
    align-items: flex-start;
    border-radius: 0 0 8px 8px;
  `,
  bottom: css`
    align-items: flex-end;
    border-radius: 8px 8px 0 0;
  `,
} as const;

export const sizeStyles = {
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

export const StyledModalContainer = styled.div<{ size: Size }>`
  background-color: #ffffff;
  min-height: 216px;
  padding: 24px 32px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  border-radius: 10px;
  position: relative;
  ${({ size }) => sizeStyles[size]};
`;

export const StyledModalWrapper = styled.div<{ position: Position }>`
  display: flex;
  flex-direction: column;
  width: 100%;
  ${({ position }) => positionStyles[position]};
`;

export const StyledCloseButtonWrapper = styled.div`
  position: absolute;
  top: 16px;
  right: 16px;
  z-index: 1;
`;
