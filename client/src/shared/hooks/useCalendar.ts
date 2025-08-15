import { useState } from 'react';

import { generateCalendarDays } from '../utils/calendar';

type CalendarMode = 'single' | 'range';

export type UseCalendarProps = {
  mode?: CalendarMode;
  initialDate?: Date;
  selectedDate?: Date | null;
  selectedEndDate?: Date | null;
  onSelectDate?: (date: Date) => void;
  onSelectDateRange?: (startDate: Date, endDate: Date) => void;
};

export const useCalendar = ({
  mode = 'single',
  initialDate = new Date(),
  selectedDate,
  selectedEndDate,
  onSelectDate,
  onSelectDateRange,
}: UseCalendarProps) => {
  const [currentMonth, setCurrentMonth] = useState(initialDate);

  const year = currentMonth.getFullYear();
  const month = currentMonth.getMonth();

  const calendarDays = generateCalendarDays(year, month);

  const goToPreviousMonth = () => {
    setCurrentMonth(new Date(year, month - 1, 1));
  };

  const goToNextMonth = () => {
    setCurrentMonth(new Date(year, month + 1, 1));
  };

  const goToMonth = (year: number, month: number) => {
    setCurrentMonth(new Date(year, month, 1));
  };

  const handleDateClick = (date: Date) => {
    if (mode === 'single') {
      onSelectDate?.(date);
      return;
    }

    if (!selectedDate) {
      onSelectDate?.(date);
    } else if (!selectedEndDate) {
      if (date.getTime() === selectedDate.getTime()) {
        onSelectDateRange?.(selectedDate, date);
      } else if (date.getTime() < selectedDate.getTime()) {
        onSelectDate?.(date);
      } else {
        onSelectDateRange?.(selectedDate, date);
      }
    } else {
      onSelectDate?.(date);
    }
  };

  return {
    currentMonth,
    year,
    month,
    calendarDays,
    mode,

    goToPreviousMonth,
    goToNextMonth,
    goToMonth,
    handleDateClick,
  };
};
