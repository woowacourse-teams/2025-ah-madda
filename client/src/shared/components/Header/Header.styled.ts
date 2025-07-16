import styled from '@emotion/styled';

export const StyledHeader = styled.header<{ justifyContent: string }>`
  position: sticky;
  top: 0;
  height: 60px;
  padding: 0 60px;
  display: flex;
  align-items: center;
  justify-content: ${({ justifyContent }) => justifyContent};
  flex-shrink: 0;
  z-index: 1;
  border-bottom: 1px solid #e5e5e5;

  @media (max-width: 768px) {
    padding: 0 20px;
  }
`;
