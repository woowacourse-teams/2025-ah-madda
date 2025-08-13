import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledWrapper = styled.div`
  width: 100%;
`;

export const StyledTextarea = styled.textarea<{ isError: boolean }>`
  background-color: #f7f7f8;
  border-radius: 8px;
  padding: 12px;
  border: none;
  font-size: 14px;
  width: 100%;
  min-height: 96px;
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
