import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

import { ButtonProps } from './Button';

const sizeStyles = {
  sm: css`
    min-height: 36px;
    padding: 0 ${theme.spacing[3]};
    font-size: 14px;
    font-weight: 500;
  `,
  md: css`
    min-height: 40px;
    padding: 0 ${theme.spacing[4]};
    font-size: 16px;
    font-weight: 500;
  `,
  lg: css`
    min-height: 48px;
    padding: 0 ${theme.spacing[5]};
    font-size: 16px;
    font-weight: 600;
  `,
  full: css`
    width: 100%;
    min-height: 48px;
    padding: 0 ${theme.spacing[5]};
    font-size: 18px;
    font-weight: 600;
    flex: 1;
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
  ghost: (color: NonNullable<ButtonProps['color']>) => css`
    color: ${color === 'primary'
      ? theme.colors.primary600
      : color === 'secondary'
        ? theme.colors.secondary600
        : theme.colors.gray800};
    background-color: transparent;
    border: none;
    transition: all 0.2s ease;

    &:hover {
      background-color: ${color === 'primary'
        ? `${theme.colors.primary600}1A`
        : color === 'secondary'
          ? `${theme.colors.secondary600}1A`
          : `${theme.colors.gray800}1A`};
    }

    &:disabled {
      color: ${theme.colors.gray400};
    }
  `,
  outline: css`
    color: ${theme.colors.gray500};
    border: 1px solid ${theme.colors.gray300};
    background-color: ${theme.colors.white};

    &:hover {
      background-color: ${theme.colors.white};
      color: ${theme.colors.gray600};
      border: 1.5px solid ${theme.colors.gray400};
    }

    &:disabled {
      border: 1px solid ${theme.colors.gray50};
      color: ${theme.colors.gray50};
    }
  `,
} as const;

export const StyledButton = styled.button<ButtonProps>`
  width: fit-content;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: ${theme.spacing[1]};
  flex-shrink: 0;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  line-height: 1.4;
  transition:
    transform 0.1s ease,
    box-shadow 0.1s ease;
  box-shadow: 0 1px 2px ${theme.colors.black}1A;

  ${({ color = 'primary', variant = 'solid' }) =>
    variant === 'outline'
      ? variantStyles.outline
      : variant === 'ghost'
        ? variantStyles.ghost(color)
        : colorStyles[color ?? 'primary']};

  ${({ size = 'md' }) => sizeStyles[size]};

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  &:active {
    transform: scale(0.97) translateY(1px);
  }
`;
