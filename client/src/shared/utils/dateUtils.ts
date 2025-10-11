export type DatePattern =
  | 'MM.DD'
  | 'MM/DD'
  | 'YY.MM.DD'
  | 'YY/MM/DD'
  | 'YYYY.MM.DD'
  | 'YYYY-MM-DD'
  | 'YYYY/MM/DD'
  | 'YYYY.MM.DD HH:mm'
  | 'YYYY-MM-DD HH:mm'
  | 'YYYY/MM/DD HH:mm'
  | 'YY.MM.DD HH:mm'
  | 'YY-MM-DD HH:mm'
  | 'YY/MM/DD HH:mm'
  | 'YYYY년 MM월 DD일'
  | 'YYYY년 MM월 DD일 HH:mm'
  | `${string}A${string}`
  | `${string}E${string}`;

/**
 * 요일 형식 옵션
 */
export type DayOfWeekFormat = 'none' | 'short' | 'shortParen' | 'long';

/**
 * 지원하는 로케일 옵션
 */
export type LocaleOption =
  | 'ko'
  | 'en'
  | 'en-US'
  | 'en-GB'
  | 'ja'
  | 'zh-CN'
  | 'zh-TW'
  | 'fr'
  | 'de'
  | 'es'
  | 'it'
  | 'pt-BR'
  | 'ru';

/**
 * 포맷 옵션
 */
type FormatOptions = {
  /**
   * @type {DayOfWeekFormat}
   * @default 'none'
   * - 'none': 요일 미표시
   * - 'short': 짧은 요일명 (e.g., 'Mon', 'Tue')
   * - 'shortParen': 짧은 요일명을 괄호로 감싸서 표시 (e.g., '(Mon)', '(Tue)')
   * - 'long': 긴 요일명 (e.g., 'Monday', 'Tuesday')
   */
  dayOfWeek?: DayOfWeekFormat;
  /**
   * @default '~' 날짜 범위 구분자
   * @type {string}
   */
  separator?: string;
  /**
   * @default 'ko' 로케일 설정 (기본값: 'ko')
   * @type {LocaleOption}
   * - 'ko': 한국어
   * - 'en', 'en-US', 'en-GB': 영어 (미국, 영국)
   * - 'ja': 일본어
   * - 'zh-CN': 중국어 (간체)
   */
  locale?: LocaleOption;
  /**
   * @default false
   * @type {boolean}
   * 시작일과 종료일이 같은 날인 경우, 날짜는 한 번만 표시하고 시간 범위만 표시
   * - true: '2025-09-24 18:00 ~ 19:41'
   * - false: '2025-09-24 18:00 ~ 2025-09-24 19:41'
   */
  smartRange?: boolean;
  /**
   * @default false
   * @type {boolean}
   * 시간을 12시간제로 표시합니다.
   * - true: 12시간제 (1~12)
   * - false: 24시간제 (0~23)
   */
  hour12?: boolean;
};

type DateRangeFormatInput = {
  /**
   * 시작 날짜
   * @type {Date | string}
   */
  start: Date | string;
  /**
   * 종료 날짜
   * @type {Date | string}
   * @default undefined
   * @optional 종료 날짜가 없을 경우 단일 날짜로 포맷팅됩니다.
   */
  end?: Date | string;
  /**
   * @default 'YYYY.MM.DD HH:mm'
   * @type {DatePattern}
   * 날짜 형식 패턴 (기본값: 'YYYY.MM.DD HH:mm')
   * - YYYY: 4자리 연도
   * - YY: 2자리 연도
   * - MM: 2자리 월 (01-12)
   * - DD: 2자리 일 (01-31)
   * - HH: 시간 (hour12 옵션에 따라 12/24시간제)
   * - mm: 2자리 분 (00-59)
   * - A: 오전/오후 (locale에 따라 다름)
   * - E: 요일 (locale 및 dayOfWeekFormat에 따라 다름)
   */
  pattern: DatePattern;
  /**
   * 포맷 옵션
   * @type {FormatOptions}
   * @default {}
   * @optional 포맷 옵션 (기본값: {})
   */
  options?: FormatOptions;
};

/**
 * 날짜 범위를 지정된 패턴과 로케일에 맞게 포맷팅합니다.
 * @param {start} params.start - 시작 날짜
 * @param {end} [params.end] - 종료 날짜 (선택사항, 없으면 단일 날짜로 표시)
 * @param {pattern} [params.pattern] - 날짜 패턴
 * @param {options} [params.options] - 포맷 옵션
 * @returns {string} 포맷팅된 날짜 범위 문자열
 *
 * @example
 * // 단일 날짜
 * formatDate({
 *   start: new Date('2025-01-15'),
 *   pattern: 'YYYY-MM-DD'
 * })
 * // '2025-01-15'
 *
 * @example
 * // 날짜 범위
 * formatDate({
 *   start: new Date('2025-01-15'),
 *   end: new Date('2025-01-20'),
 *   pattern: 'YYYY-MM-DD',
 *   options: { separator: '~' }
 * })
 * // '2025-01-15 ~ 2025-01-20'
 *
 *  @example
 * // 같은 날 시간 범위 (smartRange 사용)
 * formatDate({
 *   start: new Date('2025-09-24T18:00:00'),
 *   end: new Date('2025-09-24T19:41:00'),
 *   pattern: 'YYYY-MM-DD HH:mm',
 *   options: { smartRange: true }
 * })
 * // '2025-09-24 18:00 ~ 19:41'
 */
export const formatDate = ({ start, end, pattern, options = {} }: DateRangeFormatInput): string => {
  const {
    dayOfWeek = 'none',
    separator = '~',
    locale = 'ko',
    smartRange = false,
    hour12 = false,
  } = options;

  const startDate = typeof start === 'string' ? new Date(start) : start;
  const endDate = end ? (typeof end === 'string' ? new Date(end) : end) : null;

  if (isNaN(startDate.getTime())) {
    throw new Error('Invalid start date');
  }

  if (endDate && isNaN(endDate.getTime())) {
    throw new Error('Invalid end date');
  }

  const formatOptions = { dayOfWeek, separator, locale, hour12 };

  if (!end) return applyDatePattern(startDate, pattern, formatOptions);

  if (smartRange && endDate && isSameDay(startDate, endDate))
    return formatSameDayRange(startDate, endDate, pattern, formatOptions);

  const startStr = applyDatePattern(startDate, pattern, formatOptions);
  const endStr = applyDatePattern(endDate!, pattern, formatOptions);

  return `${startStr} ${separator} ${endStr}`;
};

/**
 * 두 날짜가 같은 날인지 확인합니다.
 * @param date1 첫 번째 Date 객체
 * @param date2 두 번째 Date 객체
 * @returns 같은 날이면 true, 아니면 false
 */
const isSameDay = (date1: Date, date2: Date) => {
  return (
    date1.getFullYear() === date2.getFullYear() &&
    date1.getMonth() === date2.getMonth() &&
    date1.getDate() === date2.getDate()
  );
};

/**
 * 같은 날의 시간 범위를 포맷팅합니다.
 * 날짜는 한 번만 표시하고 시간만 범위로 표시합니다.
 * @param startDate 시작 Date 객체
 * @param endDate 종료 Date 객체
 * @param pattern 날짜 패턴
 * @param options 포맷팅 옵션
 * @returns 포맷팅된 날짜 문자열
 * @example "2025-09-24 18:00 ~ 19:41"
 */
const formatSameDayRange = (
  startDate: Date,
  endDate: Date,
  pattern: DatePattern,
  options: FormatOptions
): string => {
  const timePattern = extractTimePattern(pattern);
  const startTimeStr = applyDatePattern(startDate, timePattern, { ...options, dayOfWeek: 'none' });
  const endTimeStr = applyDatePattern(endDate, timePattern, { ...options, dayOfWeek: 'none' });

  const datePattern = pattern
    .replace(/\s*HH:mm\s*/g, '')
    .replace(/\s*A\s*/g, '')
    .trim() as DatePattern;
  const dateOnlyStr = applyDatePattern(startDate, datePattern, options);

  return `${dateOnlyStr} ${startTimeStr} ${options.separator} ${endTimeStr}`;
};

/**
 * 패턴에서 시간 부분만 추출합니다.
 */
const extractTimePattern = (pattern: DatePattern): DatePattern => {
  const hasAmPm = pattern.includes('A');
  const hasTime = pattern.includes('HH:mm');

  if (!hasTime) return 'HH:mm' as DatePattern;

  return hasAmPm ? ('A HH:mm' as DatePattern) : ('HH:mm' as DatePattern);
};

/**
 * Intl.DateTimeFormat을 사용해 모든 날짜/시간 구성 요소를 한 번에 추출합니다.
 * @param date Date 객체
 * @param pattern 날짜 패턴
 * @param options 포맷팅 옵션
 * @returns 모든 날짜/시간 구성 요소
 */
const getAllDateParts = (date: Date, pattern: DatePattern, options: FormatOptions) => {
  const { dayOfWeek, locale, hour12 } = options;
  const use12Hour = pattern.includes('A') || hour12;
  const needWeekday = pattern.includes('E') || dayOfWeek !== 'none';
  const weekdayFormat = dayOfWeek === 'long' ? 'long' : 'short';

  const formattedParts = new Intl.DateTimeFormat(locale, {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: use12Hour ? 'numeric' : '2-digit',
    minute: '2-digit',
    weekday: needWeekday ? weekdayFormat : undefined,
    hour12: use12Hour,
  }).formatToParts(date);

  const parts = Object.fromEntries(formattedParts.map(({ type, value }) => [type, value]));

  return {
    yyyy: parts.year ?? '',
    yy: (parts.year ?? '').slice(-2),
    month: parts.month ?? '',
    day: parts.day ?? '',
    hour: parts.hour ?? '00',
    minute: parts.minute ?? '',
    weekday: parts.weekday ?? '',
    amPm: parts.dayPeriod ?? '',
  };
};

/**
 * 날짜를 지정된 패턴과 로케일에 맞게 포맷팅합니다.
 * @param date Date 객체
 * @param options 포맷팅 옵션
 * @returns 포맷팅된 날짜 문자열
 */
const applyDatePattern = (date: Date, pattern: DatePattern, options: FormatOptions) => {
  const dateParts = getAllDateParts(date, pattern, options);

  const formattedWeekday =
    options.dayOfWeek === 'shortParen' ? `(${dateParts.weekday})` : dateParts.weekday;

  const tokenMap: Record<string, string> = {
    YYYY: dateParts.yyyy,
    YY: dateParts.yy,
    MM: dateParts.month,
    DD: dateParts.day,
    HH: dateParts.hour,
    mm: dateParts.minute,
    A: dateParts.amPm,
    E: formattedWeekday,
  };

  return pattern.replace(/YYYY|YY|MM|DD|HH|mm|A|E/g, (match) => tokenMap[match] || match);
};
