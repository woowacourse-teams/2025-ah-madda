import { css } from '@emotion/react';
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
};

export const DateButton = styled(Button)<DateButtonProps>`
  width: 32px;
  height: 32px;
  transition: all 0.2s ease;
  background-color: white;

  ${({ isCurrentMonth }) =>
    !isCurrentMonth &&
    css`
      color: ${theme.colors.gray400};
    `}

  ${({ isCurrentMonth, isWeekend }) =>
    isCurrentMonth &&
    css`
      color: ${isWeekend ? `${theme.colors.red500}` : `${theme.colors.gray900}`};
    `}
  
  ${({ isToday }) =>
    isToday &&
    css`
      background-color: ${theme.colors.gray100};
      color: ${theme.colors.gray900};
    `}
  
  
  ${({ isSelected }) =>
    isSelected &&
    css`
      background-color: ${theme.colors.primary300};
      color: white;
    `}
  
  ${({ isInRange }) =>
    isInRange &&
    css`
      background-color: ${theme.colors.primary100};
      color: ${theme.colors.primary600};
    `}
  
  &:hover {
    ${({ isSelected }) =>
      !isSelected &&
      css`
        background-color: ${theme.colors.gray100};
      `}
  }
`;

export const WeekDay = styled(Flex)`
  display: flex;
  justify-content: center;
`;

export const WeekDayHeader = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  margin: 0 0 8px 0;
`;

export const DateContainer = styled.div`
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  gap: 1px;
`;
