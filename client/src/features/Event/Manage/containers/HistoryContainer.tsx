import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

type HistoryContainerProps = {
  children: React.ReactNode;
};

export const HistoryContainer = ({ children }: HistoryContainerProps) => {
  return <StyledHistoryContainer>{children}</StyledHistoryContainer>;
};
const StyledHistoryContainer = styled.div`
  max-height: 300px;
  overflow-y: auto;

  &::-webkit-scrollbar {
    width: 8px;
  }

  &::-webkit-scrollbar-thumb {
    background: ${theme.colors.gray300};
    border-radius: 4px;
  }
`;
