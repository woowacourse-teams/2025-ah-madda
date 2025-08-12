import { useState } from 'react';

import { css } from '@emotion/react';

import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Card } from '../Card';
import { Flex } from '../Flex';
import { IconButton } from '../IconButton';

import { WeekDay, DateButton, DateContainer, WeekDayHeader } from './Calendar.styled';

type CalendarProps = {
  selectedDate: Date | null;
  selectedEndDate: Date | null;
  onSelectDate: (date: Date) => void;
  onSelectDateRange: (startDate: Date, endDate: Date) => void;
  mode: 'single' | 'range';
};

const DAYS_OF_WEEK = ['일', '월', '화', '수', '목', '금', '토'];
const MONTHS = [
  '1월',
  '2월',
  '3월',
  '4월',
  '5월',
  '6월',
  '7월',
  '8월',
  '9월',
  '10월',
  '11월',
  '12월',
];

const FIRST_DAY_OF_MONTH = 1;
const SATURDAY_DAY_INDEX = 6;

export const Calendar = ({
  selectedDate,
  selectedEndDate,
  onSelectDate,
  onSelectDateRange,
  mode = 'single',
}: CalendarProps) => {
  const [currentMonth, setCurrentMonth] = useState(selectedDate || new Date());

  const today = new Date();
  const year = currentMonth.getFullYear();
  const month = currentMonth.getMonth();

  const firstDayMonth = new Date(year, month, 1);
  const lastDayMonth = new Date(year, month + 1, 0);

  const startDate = new Date(firstDayMonth);
  startDate.setDate(FIRST_DAY_OF_MONTH - firstDayMonth.getDay());

  const endDate = new Date(lastDayMonth);
  endDate.setDate(endDate.getDate() + (SATURDAY_DAY_INDEX - lastDayMonth.getDay()));

  const days = [];
  const currenDate = new Date(startDate);

  while (currenDate <= endDate) {
    days.push(new Date(currenDate));
    currenDate.setDate(currenDate.getDate() + 1);
  }

  const handlePrevMonth = () => {
    setCurrentMonth(new Date(year, month - 1, 1));
  };

  const handleNextMonth = () => {
    setCurrentMonth(new Date(year, month + 1, 1));
  };

  const handleDateClick = (date: Date) => {
    if (mode === 'single') {
      onSelectDate?.(date);
      return;
    }

    if (!selectedDate) {
      onSelectDate?.(date);
    } else if (!selectedEndDate || date.getTime() === selectedDate.getTime()) {
      if (date.getTime() < selectedDate.getTime()) {
        onSelectDate?.(date);
      } else {
        onSelectDateRange?.(selectedDate, date);
      }
    } else {
      onSelectDate?.(date);
    }
  };

  const isToday = (date: Date) => {
    return date.toDateString() === today.toDateString();
  };

  const isSelected = (date: Date) => {
    const dateString = date.toDateString();
    const isStart = selectedDate && dateString === selectedDate.toDateString();
    const isEnd = selectedEndDate && dateString === selectedEndDate.toDateString();
    return isStart || isEnd;
  };

  const isInRange = (date: Date) => {
    if (mode !== 'range' || !selectedDate || !selectedEndDate) return false;
    return date > selectedDate && date < selectedEndDate;
  };

  const isCurrentMonth = (date: Date) => {
    return date.getMonth() === month;
  };

  return (
    <Card
      css={css`
        width: 280px;
        padding: 16px;
      `}
    >
      <Flex dir="row" alignItems="center" justifyContent="space-between" margin="0 0 16px 0">
        <IconButton name="back" size={16} color="gray" onClick={handlePrevMonth} />
        <Text as="h3" type="Body" weight="medium" color="black">
          {year}년 {MONTHS[month]}
        </Text>
        <IconButton
          name="back"
          size={16}
          color="gray"
          onClick={handleNextMonth}
          css={css`
            transform: scaleX(-1);
          `}
        />
      </Flex>

      <WeekDayHeader>
        {DAYS_OF_WEEK.map((day, index) => (
          <WeekDay key={day}>
            <Text
              type="Label"
              weight="medium"
              color={
                index === 0 || index === 6 ? `${theme.colors.red500}` : `${theme.colors.gray900}`
              }
            >
              {day}
            </Text>
          </WeekDay>
        ))}
      </WeekDayHeader>

      <DateContainer>
        {days.map((date, index) => (
          <DateButton
            key={index}
            onClick={() => handleDateClick(date)}
            isToday={isToday(date)}
            isSelected={isSelected(date) || false}
            isInRange={isInRange(date)}
            isCurrentMonth={isCurrentMonth(date)}
            isWeekend={date.getDay() === 0 || date.getDay() === 6}
          >
            {date.getDate()}
          </DateButton>
        ))}
      </DateContainer>
    </Card>
  );
};
