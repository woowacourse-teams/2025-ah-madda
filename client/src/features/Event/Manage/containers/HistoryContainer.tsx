import styled from '@emotion/styled';

type HistoryContainerProps = {
  children: React.ReactNode;
};

export const HistoryContainer = ({ children }: HistoryContainerProps) => {
  return <StyledHistoryContainer>{children}</StyledHistoryContainer>;
};
const StyledHistoryContainer = styled.div`
  max-height: 300px;
  overflow-y: auto;
`;
