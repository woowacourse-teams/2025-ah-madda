import { css } from '@emotion/react';
import styled from '@emotion/styled';

export const StyledTabs = styled.div`
  display: flex;
  flex-direction: column;
`;

type StyledTabsListProps = {
  tabCount: number;
};

export const StyledTabsList = styled.div<StyledTabsListProps>`
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
    width: 0;
  }

  ${({ tabCount }) => createTabsAnimation(tabCount)}
`;

export const createTabsAnimation = (tabCount: number) => css`
  ${Array.from(
    { length: tabCount },
    (_, index) => `
    &:has([data-active]:nth-child(${index + 1})[data-active='true'])::after {
      left: calc(${index * 100}% / ${tabCount});
      width: calc(100% / ${tabCount});
    }
  `
  ).join('')}
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
    background-color: ${({ theme }) => theme.colors.primary50};
  }

  &[data-active='true'] {
    background-color: transparent;
    color: ${({ theme }) => theme.colors.gray900};
    box-shadow: none;
    transform: none;
  }
`;

export const StyledTabsContent = styled.div``;
