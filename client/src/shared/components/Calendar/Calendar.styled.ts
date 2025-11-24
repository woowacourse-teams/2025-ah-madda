import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

import { Button } from '../Button';
import { Flex } from '../Flex';

type DateButtonProps = {
  isToday: boolean;
  isSelected: boolean;
  isCurrentMonth: boolean;
  isWeekend: boolean;
  isInRange?: boolean;
  disabled?: boolean;
};

export const DateButton = styled(Button)<DateButtonProps>`
  width: 24px;
  height: 24px;
  transition: all 0.2s ease;
  background-color: ${({ isToday, isSelected, isInRange }) => {
    if (isSelected) return theme.colors.primary300;
    if (isInRange) return theme.colors.primary100;
    if (isToday) return theme.colors.gray100;
    return 'white';
  }};
  color: ${({ isCurrentMonth, isWeekend, isToday, isSelected, isInRange, disabled }) => {
    if (disabled) return theme.colors.gray400;
    if (isSelected) return 'white';
    if (isInRange) return theme.colors.primary600;
    if (isToday) return theme.colors.gray900;
    if (!isCurrentMonth) return theme.colors.gray400;
    return isWeekend ? theme.colors.red500 : theme.colors.gray900;
  }};

  &:hover:not(:disabled) {
    background-color: ${({ isSelected }) =>
      isSelected ? theme.colors.primary300 : theme.colors.gray100};
  }

  &:disabled {
    cursor: not-allowed;
    opacity: 0.6;
  }
`;

export const WeekDay = styled(Flex)`
  justify-content: center;
`;

export const WeekDayHeader = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin: 0 0 6px 0;
`;

export const DateContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  justify-items: center;
  gap: 1px;
`;
