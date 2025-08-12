import { css } from '@emotion/react';

import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { useCalendar } from '../../hooks/useCalendar';
import {
  DAYS_OF_WEEK,
  MONTHS,
  isToday,
  isCurrentMonth,
  isWeekend,
  isSelectedDate,
  isInDateRange,
} from '../../utils/calendar';
import { Card } from '../Card';
import { Flex } from '../Flex';
import { IconButton } from '../IconButton';

import { WeekDay, DateButton, DateContainer, WeekDayHeader } from './Calendar.styled';

type CalendarProps = {
  selectedDate?: Date | null;
  selectedEndDate?: Date | null;
  onSelectDate?: (date: Date) => void;
  onSelectDateRange?: (startDate: Date, endDate: Date) => void;
  mode?: 'single' | 'range';
};

export const Calendar = ({
  selectedDate,
  selectedEndDate,
  onSelectDate,
  onSelectDateRange,
  mode = 'single',
}: CalendarProps) => {
  const { year, month, calendarDays, goToPreviousMonth, goToNextMonth, handleDateClick } =
    useCalendar({
      mode,
      initialDate: selectedDate || new Date(),
      selectedDate,
      selectedEndDate,
      onSelectDate,
      onSelectDateRange,
    });

  return (
    <Card
      css={css`
        width: 280px;
        padding: 16px;
      `}
    >
      <Flex dir="row" alignItems="center" justifyContent="space-between" margin="0 0 16px 0">
        <IconButton name="back" size={16} color="gray" onClick={goToPreviousMonth} />
        <Text as="h3" type="Body" weight="medium" color="black">
          {year}년 {MONTHS[month]}
        </Text>
        <IconButton
          name="back"
          size={16}
          color="gray"
          onClick={goToNextMonth}
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
        {calendarDays.map((date, index) => (
          <DateButton
            key={index}
            onClick={() => handleDateClick(date)}
            isToday={isToday(date)}
            isSelected={isSelectedDate(date, selectedDate || null, selectedEndDate || null)}
            isInRange={isInDateRange(date, selectedDate || null, selectedEndDate || null)}
            isCurrentMonth={isCurrentMonth(date, month)}
            isWeekend={isWeekend(date)}
          >
            {date.getDate()}
          </DateButton>
        ))}
      </DateContainer>
    </Card>
  );
};
