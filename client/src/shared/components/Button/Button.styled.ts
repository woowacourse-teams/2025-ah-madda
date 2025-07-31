import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

import { ButtonProps } from './Button';

const sizeStyles = {
  sm: css`
    width: 80px;
    height: 32px;
    font-size: 16px;
    font-weight: 500;
  `,
  md: css`
    width: 138px;
    height: 42px;
    font-size: 16px;
    font-weight: 600;
  `,
  lg: css`
    width: 240px;
    height: 50px;
    font-size: 16px;
    font-weight: 600;
  `,
  full: css`
    width: 100%;
    height: 50px;
    font-size: 16px;
    font-weight: 600;
  `,
} as const;

const colorStyles = {
  primary: css`
    color: white;
    background-color: ${theme.colors.primary600};

    &:hover {
      background-color: ${theme.colors.primary700};
    }

    &:disabled {
      background-color: ${theme.colors.gray400};
      color: ${theme.colors.gray200};
    }
  `,
  secondary: css`
    color: ${theme.colors.primary600};
    background-color: ${theme.colors.primary50};

    &:hover {
      background-color: ${theme.colors.primary100};
    }

    &:disabled {
      background-color: ${theme.colors.gray200};
      color: ${theme.colors.gray400};
    }
  `,
  tertiary: css`
    color: white;
    background-color: ${theme.colors.gray800};

    &:hover {
      background-color: ${theme.colors.gray900};
    }

    &:disabled {
      background-color: ${theme.colors.gray600};
      color: ${theme.colors.gray400};
    }
  `,
} as const;

const variantStyles = {
  outlined: css`
    color: ${theme.colors.gray500};
    border: 1px solid ${theme.colors.gray300};
    background-color: transparent;

    &:hover {
      background-color: white;
      color: ${theme.colors.gray600};
      border: 1px solid ${theme.colors.gray400};
      opacity: 0.8;
    }

    &:disabled {
      border: 1px solid ${theme.colors.gray50};
      color: ${theme.colors.gray50};
    }
  `,
};

export const StyledButton = styled.button<ButtonProps>`
  height: 40px;
  cursor: pointer;
  background-color: transparent;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: ${({ iconName }) => (iconName ? '2px' : '0')};
  border: none;
  border-radius: 4px;
  line-height: 1.4;

  ${({ size }) => sizeStyles[size ?? 'md']}
  ${({ color, variant = 'solid' }) =>
    variant === 'outline' ? variantStyles.outlined : colorStyles[color ?? 'primary']}

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }
`;
