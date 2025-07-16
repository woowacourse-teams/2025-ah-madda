import styled from '@emotion/styled';

export const StyledHeader = styled.header<{ justifyContent: string }>`
  position: sticky;
  top: 0;
  height: 30px;
  padding: 16px;
  display: flex;
  align-items: center;
  justify-content: ${({ justifyContent }) => justifyContent};
  flex-shrink: 0;
  z-index: 1;
  border-bottom: 1px solid #e5e5e5;
`;
