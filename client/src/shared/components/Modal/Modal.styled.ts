import { css } from '@emotion/react';
import styled from '@emotion/styled';

export type Position = 'center' | 'top' | 'bottom';
export type Size = 'small' | 'medium' | 'large';

export const positionStyles = {
  center: css`
    align-items: center;
  `,
  top: css`
    align-items: flex-start;
  `,
  bottom: css`
    align-items: flex-end;
  `,
} as const;

export const radiusStyles = {
  center: css`
    border-radius: 8px;
  `,
  top: css`
    border-radius: 0 0 8px 8px;
  `,
  bottom: css`
    border-radius: 8px 8px 0 0;
  `,
} as const;

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

export const StyledModalLayout = styled.div<{ position: Position }>`
  position: fixed;
  inset: 0;
  background-color: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  ${({ position }) => positionStyles[position]};
  z-index: 1000;
`;

export const StyledModalContainer = styled.div<{ size: Size; position: Position }>`
  background-color: #ffffff;
  min-height: 216px;
  padding: 24px 32px;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);
  position: relative;
  ${({ position }) => radiusStyles[position]};
  ${({ size }) => sizeStyles[size]};
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
