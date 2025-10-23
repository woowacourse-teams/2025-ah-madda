import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledWrapper = styled.div`
  width: 100%;
`;

export const StyledFieldWrapper = styled.div`
  position: relative;
`;

export const StyledInput = styled.input<{
  isError?: boolean;
  hasLeftIcon?: boolean;
  hasRightIcon?: boolean;
}>`
  box-sizing: border-box;
  background-color: ${theme.colors.white};
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  width: 100%;
  border: none;
  outline: 1.5px solid ${theme.colors.gray300};
  transition: all 0.15s ease;

  ${({ isError }) =>
    isError &&
    css`
      outline-color: ${theme.colors.red500};
      box-shadow: 0 0 0 4px ${theme.colors.red100};
    `}

  ${({ isError }) =>
    !isError &&
    css`
      &:focus {
        outline: 1.5px solid ${theme.colors.primary700};
        box-shadow: 0 0 0 4px ${theme.colors.primary100};
      }
    `}

  ${({ hasLeftIcon }) =>
    hasLeftIcon &&
    css`
      padding-left: 40px;
    `}

  ${({ hasRightIcon }) =>
    hasRightIcon &&
    css`
      padding-right: 40px;
    `}

  &::-webkit-calendar-picker-indicator {
    opacity: 0;
    pointer-events: none;
  }

  @supports (-webkit-touch-callout: none) {
    font-size: 16px;
  }
`;

export const StyledHelperText = styled.p<{ isError: boolean }>`
  font-size: 14px;
  min-height: 18px;
  color: ${({ isError }) => (isError ? theme.colors.red300 : theme.colors.gray400)};
`;

export const StyledCalendarButton = styled.button`
  position: absolute;
  left: 12px;
  top: 50%;
  transform: translateY(-50%);
  border: 0;
  background: transparent;
  padding: 0;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
`;

export const StyledClearButton = styled.button`
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  border: 0;
  background: transparent;
  padding: 0;
  display: inline-flex;
  align-items: center;
  cursor: pointer;
`;

export const StyledFooterRow = styled.div`
  width: 100%;
  margin-top: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const StyledCounterText = styled.p`
  font-size: 14px;
  color: ${theme.colors.gray600};
`;
