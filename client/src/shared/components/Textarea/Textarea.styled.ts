import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledWrapper = styled.div`
  width: 100%;
`;

export const StyledTextarea = styled.textarea<{ isError: boolean }>`
  background-color: ${theme.colors.gray50};
  border-radius: 8px;
  padding: 16px;
  border: 1px solid ${theme.colors.gray200};
  font-size: 14px;
  width: 100%;
  min-height: 100px;
  resize: vertical;
  line-height: 1.5;

  &:focus {
    outline: none;
    border: 1px solid ${({ isError }) => (isError ? theme.colors.red300 : theme.colors.gray400)};
  }
`;

export const StyledHelperText = styled.p<{ isError: boolean }>`
  margin-top: 4px;
  font-size: 12px;
  min-height: 18px;
  color: ${({ isError }) => (isError ? theme.colors.red300 : theme.colors.gray400)};
`;

export const StyledFooterRow = styled.div`
  /* padding-top: 8px; */
  display: flex;
  align-items: center;
`;

export const StyledCounterText = styled.span`
  margin-left: auto;
  font-size: 14px;
  color: #99a1af;
  white-space: nowrap;
`;
