import styled from '@emotion/styled';

export const StyledTabs = styled.div`
  display: flex;
  flex-direction: column;
`;

export const StyledTabsList = styled.div`
  display: flex;
  background-color: transparent;
  position: relative;

  &::after {
    content: '';
    position: absolute;
    bottom: 0;
    left: 0;
    height: 3px;
    background-color: ${({ theme }) => theme.colors.gray900};
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
`;

export const StyledTabsTrigger = styled.button`
  border-radius: 0;
  padding: 16px 20px;
  font-size: 16px;
  font-weight: 600;
  width: 100%;
  background: transparent;
  border: none;
  cursor: pointer;
  transition:
    color 0.3s cubic-bezier(0.4, 0, 0.2, 1),
    background-color 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  color: ${({ theme }) => theme.colors.gray500};

  &:hover {
    color: ${({ theme }) => theme.colors.gray700};
    background-color: ${({ theme }) => theme.colors.gray100};
  }

  &[data-active='true'] {
    background-color: transparent;
    color: ${({ theme }) => theme.colors.gray900};
    box-shadow: none;
    transform: none;
  }
`;

export const StyledTabsContent = styled.div``;
