import styled from '@emotion/styled';

export const StyledHeader = styled.header<{ hasRight: boolean }>`
  position: sticky;
  top: 0;
  width: 100%;
  height: 60px;
  padding: 0 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  justify-content: ${({ hasRight }) => (hasRight ? 'space-between' : 'flex-start')};
  flex-shrink: 0;
  z-index: 10;
  border-bottom: 1px solid #e5e5e5;
  background-color: #fff;

  @media (max-width: 768px) {
    padding: 0 20px;
  }
`;
