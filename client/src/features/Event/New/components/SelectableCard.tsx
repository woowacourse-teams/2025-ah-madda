import { ReactNode } from 'react';

import styled from '@emotion/styled';

import { Card } from '@/shared/components/Card';
import { theme } from '@/shared/styles/theme';

type SelectableCardProps = {
  isSelected: boolean;
  onClick: () => void;
  children: ReactNode;
};

export const SelectableCard = ({ isSelected, onClick, children }: SelectableCardProps) => {
  return (
    <StyledCard isSelected={isSelected} onClick={onClick}>
      {children}
    </StyledCard>
  );
};

type StyledCardProps = {
  isSelected: boolean;
};

const StyledCard = styled(Card)<StyledCardProps>`
  cursor: pointer;
  padding: 16px;
  background-color: ${({ isSelected }) =>
    isSelected ? theme.colors.primary50 : theme.colors.white};
  border-radius: 8px;
  transition: all 0.2s ease;
  &:hover {
    background-color: ${theme.colors.primary50};
  }
`;
