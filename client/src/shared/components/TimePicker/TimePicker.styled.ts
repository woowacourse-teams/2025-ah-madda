import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledTimePicker = styled.div`
  display: flex;
  flex-direction: column;
  width: 100%;
  gap: 8px;
  padding: 12px;

  @media (max-width: 768px) {
    gap: 6px;
    padding: 8px;
  }
`;

export const StyledSelect = styled.select`
  padding: 8px 32px 8px 12px;
  border: 1px solid ${theme.colors.gray300};
  border-radius: 4px;
  background-color: ${theme.colors.white};
  color: ${theme.colors.gray900};
  cursor: pointer;

  &:focus {
    border-color: ${theme.colors.primary400};
  }

  &:hover:not(:disabled) {
    border-color: ${theme.colors.gray400};
  }
`;
