import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledWrapper = styled.div`
  width: 100%;
`;

export const StyledTextarea = styled.textarea<{ isError: boolean }>`
  box-sizing: border-box;
  background-color: ${theme.colors.white};
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  width: 100%;
  min-height: 140px;
  resize: vertical;
  line-height: 1.5;
  border: none;
  outline: 1.5px solid ${theme.colors.gray300};
  transition: all 0.15s ease;

  &:focus {
    outline: 1.5px solid ${theme.colors.primary700};
    box-shadow: 0 0 0 4px ${theme.colors.primary100};
  }

  ${({ isError }) =>
    isError &&
    css`
      outline-color: ${theme.colors.red500};
      box-shadow: 0 0 0 4px ${theme.colors.red100};
    `}

  @supports (-webkit-touch-callout: none) {
    font-size: 16px;
  }
`;

export const StyledHelperText = styled.p<{ isError: boolean }>`
  margin-top: 4px;
  font-size: 12px;
  min-height: 18px;
  color: ${({ isError }) => (isError ? theme.colors.red300 : theme.colors.gray400)};
`;

export const StyledFooterRow = styled.div`
  display: flex;
  align-items: center;
  margin-top: 8px;
`;

export const StyledCounterText = styled.span`
  margin-left: auto;
  font-size: 14px;
  color: ${theme.colors.gray600};
`;
