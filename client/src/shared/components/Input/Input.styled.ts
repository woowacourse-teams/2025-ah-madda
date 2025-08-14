import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledWrapper = styled.div`
  width: 100%;
`;

export const StyledLabel = styled.label`
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 20px;
  margin-bottom: 8px;
  font-size: 14px;
`;

export const StyledRequiredMark = styled.span`
  color: red;
  font-size: 14px;
`;

export const StyledFieldWrapper = styled.div`
  position: relative;
`;

export const StyledInput = styled.input<{ isError?: boolean; hasLeftIcon?: boolean }>`
  background-color: ${theme.colors.gray50};
  border-radius: 8px;
  padding: 12px;
  border: 1px solid ${theme.colors.gray200};
  font-size: 14px;
  width: 100%;

  &:focus {
    outline: none;
    border: 1px solid ${({ isError }) => (isError ? theme.colors.red300 : theme.colors.gray400)};
  }

  ${({ hasLeftIcon }) =>
    hasLeftIcon &&
    css`
      padding-left: 40px;
    `}

  &::-webkit-calendar-picker-indicator {
    opacity: 0;
    pointer-events: none;
  }

  ${({ isError, theme }) =>
    isError &&
    css`
      border-color: ${theme.colors.red500};
    `}
`;

export const StyledHelperText = styled.p<{ isError: boolean }>`
  margin-top: 4px;
  font-size: 12px;
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

export const StyledFooterRow = styled.div`
  display: flex;
  justify-content: space-between;
  align-items: center;
`;

export const StyledCounterText = styled.p`
  font-size: 14px;
  color: ${theme.colors.gray400};
`;
