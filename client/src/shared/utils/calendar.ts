export const DAYS_OF_WEEK = ['일', '월', '화', '수', '목', '금', '토'] as const;
export const MONTHS = [
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
] as const;

const FIRST_DAY_OF_MONTH = 1;
const SATURDAY_DAY_INDEX = 6;

export const generateCalendarDays = (year: number, month: number): Date[] => {
  const firstDayMonth = new Date(year, month, 1);
  const lastDayMonth = new Date(year, month + 1, 0);

  const startDate = new Date(firstDayMonth);
  startDate.setDate(FIRST_DAY_OF_MONTH - firstDayMonth.getDay());

  const endDate = new Date(lastDayMonth);
  endDate.setDate(endDate.getDate() + (SATURDAY_DAY_INDEX - lastDayMonth.getDay()));

  const days: Date[] = [];
  const currentDate = new Date(startDate);

  while (currentDate <= endDate) {
    days.push(new Date(currentDate));
    currentDate.setDate(currentDate.getDate() + 1);
  }

  return days;
};

export const isSameDay = (a: Date, b: Date): boolean => {
  return (
    a.getFullYear() === b.getFullYear() &&
    a.getMonth() === b.getMonth() &&
    a.getDate() === b.getDate()
  );
};

export const isToday = (date: Date): boolean => {
  const today = new Date();
  return isSameDay(date, today);
};

export const isCurrentMonth = (date: Date, currentMonth: number): boolean => {
  return date.getMonth() === currentMonth;
};

export const isWeekend = (date: Date): boolean => {
  const dayOfWeek = date.getDay();
  return dayOfWeek === 0 || dayOfWeek === 6;
};

export const isSelectedDate = (
  date: Date,
  selectedDate: Date | null,
  selectedEndDate: Date | null
): boolean => {
  if (!selectedDate) return false;

  const isStart = isSameDay(date, selectedDate);
  const isEnd = selectedEndDate ? isSameDay(date, selectedEndDate) : false;

  return isStart || isEnd;
};

export const isInDateRange = (
  date: Date,
  selectedDate: Date | null,
  selectedEndDate: Date | null
): boolean => {
  if (!selectedDate || !selectedEndDate) return false;
  return date > selectedDate && date < selectedEndDate;
};
