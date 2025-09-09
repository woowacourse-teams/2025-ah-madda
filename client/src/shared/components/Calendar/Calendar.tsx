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
  disabledDates?: Date[];
};

export const Calendar = ({
  selectedDate,
  selectedEndDate,
  onSelectDate,
  onSelectDateRange,
  mode = 'single',
  disabledDates = [],
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

  const isDisabled = (date: Date) => {
    const today = new Date();
    const todayStart = new Date(today.getFullYear(), today.getMonth(), today.getDate());
    const dateStart = new Date(date.getFullYear(), date.getMonth(), date.getDate());

    if (dateStart < todayStart) {
      return true;
    }

    return disabledDates.some((disabledDate) => {
      const disabledDateStart = new Date(
        disabledDate.getFullYear(),
        disabledDate.getMonth(),
        disabledDate.getDate()
      );
      return dateStart > disabledDateStart;
    });
  };

  return (
    <Card
      css={css`
        width: 280px;
        padding: 12px;

        @media (max-width: 768px) {
          width: 100%;
          padding: 8px;
        }
      `}
    >
      <Flex dir="row" alignItems="center" justifyContent="space-between" margin="0 0 12px 0">
        <IconButton name="back" size={16} color="gray" onClick={goToPreviousMonth} />
        <Text as="h3" type="Body" weight="medium" color="black">
          {year}년 {MONTHS[month]}
        </Text>
        <IconButton name="next" size={16} color="gray" onClick={goToNextMonth} />
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
        {calendarDays.map((date) => (
          <DateButton
            key={date.toISOString()}
            onClick={() => handleDateClick(date)}
            isToday={isToday(date)}
            isSelected={isSelectedDate(date, selectedDate || null, selectedEndDate || null)}
            isInRange={isInDateRange(date, selectedDate || null, selectedEndDate || null)}
            isCurrentMonth={isCurrentMonth(date, month)}
            isWeekend={isWeekend(date)}
            disabled={isDisabled(date)}
          >
            {date.getDate()}
          </DateButton>
        ))}
      </DateContainer>
    </Card>
  );
};
