import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { ButtonProps } from './Button';

const sizeStyles = {
  sm: css`
    padding: 0.375rem 0.75rem;
    font-size: 0.875rem;
  `,
  md: css`
    padding: 0.5rem 1rem;
    font-size: 1rem;
  `,
  lg: css`
    padding: 0.625rem 1.25rem;
    font-size: 1.125rem;
  `,
  xl: css`
    padding: 0.75rem 1.5rem;
    font-size: 1.25rem;
  `,
} as const;

const variantStyles = {
  filled: (color: string) => css`
    background-color: ${color};
    &:hover {
      background-color: ${color};
      opacity: 0.7;
    }
  `,
  outlined: (color: string) => css`
    border: 1px solid ${color};
    background-color: transparent;

    &:hover {
      background-color: ${color.startsWith('#') ? `${color}1A` : `rgba(0, 0, 0, 0.1)`};
      opacity: 0.8;
    }
  `,
};

export const StyledButton = styled.button<ButtonProps>`
  cursor: pointer;
  font-weight: 500;
  background-color: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
  padding: 0.5rem 1rem;
  border: none;
  border-radius: 0.75rem;
  line-height: 1.4;
  width: ${({ width }) => width ?? 'auto'};
  color: ${({ fontColor }) => fontColor ?? 'white'};

  ${({ size }) => sizeStyles[size ?? 'md']}
  ${({ variant, color }) => variantStyles[variant ?? 'filled'](color ?? 'black')};

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;
