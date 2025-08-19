import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledDropdownContainer = styled.div`
  position: relative;
`;

export const StyledContentContainer = styled.div`
  position: absolute;
  top: 100%;
  z-index: 1000;
  background: ${theme.colors.white};
  border: 1px solid ${theme.colors.gray200};
  border-radius: 6px;
  max-height: 160px;
  overflow-y: auto;
  width: 100%;
`;
