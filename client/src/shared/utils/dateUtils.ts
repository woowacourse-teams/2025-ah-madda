export type DatePattern =
  | 'MM.DD'
  | 'MM/DD'
  | 'YYYY-MM-DD'
  | 'YY.MM.DD'
  | 'YYYY.MM.DD'
  | 'YYYY-MM-DD HH:mm'
  | 'YYYY.MM.DD HH:mm'
  | 'YY.MM.DD HH:mm'
  | 'YYYY-MM-DD h:mm'
  | 'YYYY.MM.DD h:mm'
  | 'YY.MM.DD h:mm'
  | 'YYYY년 MM월 DD일 h:mm'
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
   * @default 'YYYY.MM.DD HH:mm'
   * @type {DatePattern}
   * 날짜 형식 패턴 (기본값: 'YYYY.MM.DD HH:mm')
   * - YYYY: 4자리 연도
   * - YY: 2자리 연도
   * - MM: 2자리 월 (01-12)
   * - DD: 2자리 일 (01-31)
   * - HH: 2자리 시간 (00-23)
   * - mm: 2자리 분 (00-59)
   * - A: 오전/오후 (locale에 따라 다름)
   * - h: 12시간제 시간 (1-12)
   * - E: 요일 (locale 및 dayOfWeekFormat에 따라 다름)
   */
  pattern?: DatePattern;
  /**
   * @type {DayOfWeekFormat}
   * @default 'none'
   * - 'none': 요일 미표시
   * - 'short': 짧은 요일명 (e.g., 'Mon', 'Tue')
   * - 'shortParen': 짧은 요일명을 괄호로 감싸서 표시 (e.g., '(Mon)', '(Tue)')
   * - 'long': 긴 요일명 (e.g., 'Monday', 'Tuesday')
   */
  dayOfWeekFormat?: DayOfWeekFormat;
  /**
   * @default '~' 날짜 범위 구분자
   * @type {string}
   */
  rangeSeparator?: string;
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
 * @param {options} [params.options] - 포맷 옵션
 * @returns {string} 포맷팅된 날짜 범위 문자열
 *
 * @example
 * // 단일 날짜
 * formatDate({
 *   start: new Date('2025-01-15'),
 *   options: { pattern: 'YYYY-MM-DD' }
 * })
 * // '2025-01-15'
 *
 * @example
 * // 날짜 범위
 * formatDate({
 *   start: new Date('2025-01-15'),
 *   end: new Date('2025-01-20'),
 *   options: { pattern: 'YYYY-MM-DD', rangeSeparator: '~' }
 * })
 * // '2025-01-15 ~ 2025-01-20'
 *
 *  @example
 * // 같은 날 시간 범위 (smartRange 사용)
 * formatDate({
 *   start: new Date('2025-09-24T18:00:00'),
 *   end: new Date('2025-09-24T19:41:00'),
 *   options: { pattern: 'YYYY-MM-DD HH:mm', smartRange: true }
 * })
 * // '2025-09-24 18:00 ~ 19:41'
 */
export const formatDate = ({ start, end, options = {} }: DateRangeFormatInput): string => {
  const {
    pattern = 'YYYY.MM.DD HH:mm',
    dayOfWeekFormat = 'none',
    rangeSeparator = '~',
    locale = 'ko',
    smartRange = false,
  } = options;

  const startDate = typeof start === 'string' ? new Date(start) : start;
  const endDate = end ? (typeof end === 'string' ? new Date(end) : end) : null;

  if (isNaN(startDate.getTime())) {
    throw new Error('Invalid start date');
  }

  if (endDate && isNaN(endDate.getTime())) {
    throw new Error('Invalid end date');
  }

  if (!end) {
    return applyDatePattern(startDate, pattern, locale, dayOfWeekFormat);
  }
  if (smartRange && endDate && isSameDay(startDate, endDate)) {
    return formatSameDayRange(startDate, endDate, pattern, locale, dayOfWeekFormat, rangeSeparator);
  }
  const startStr = applyDatePattern(startDate, pattern, locale, dayOfWeekFormat);
  const endStr = applyDatePattern(endDate!, pattern, locale, dayOfWeekFormat);

  return `${startStr} ${rangeSeparator} ${endStr}`;
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
 * @param options 날짜 형식 패턴, 로케일, 요일 형식, 범위 구분자
 * @returns 포맷팅된 날짜 문자열
 * @example "2025-09-24 18:00 ~ 19:41"
 */
const formatSameDayRange = (
  startDate: Date,
  endDate: Date,
  pattern: DatePattern,
  locale: LocaleOption,
  dayOfWeekFormat: DayOfWeekFormat,
  rangeSeparator: string
): string => {
  const timePattern = extractTimePattern(pattern);

  const startTimeStr = applyDatePattern(startDate, timePattern, locale, 'none');
  const endTimeStr = applyDatePattern(endDate, timePattern, locale, 'none');

  const datePattern = pattern
    .replace(/\s*HH:mm\s*/g, '')
    .replace(/\s*h:mm\s*/g, '')
    .replace(/\s*A\s*/g, '')
    .trim() as DatePattern;

  const dateOnlyStr = applyDatePattern(startDate, datePattern, locale, dayOfWeekFormat);

  return `${dateOnlyStr} ${startTimeStr} ${rangeSeparator} ${endTimeStr}`;
};

/**
 * 패턴에서 시간 부분만 추출합니다.
 * @param pattern 전체 날짜 패턴
 * @returns 시간 패턴만 포함된 문자열
 */
const extractTimePattern = (pattern: DatePattern): DatePattern => {
  if (/A.*h:mm|h:mm.*A/.test(pattern)) {
    return 'A h:mm' as DatePattern;
  }
  if (pattern.includes('HH:mm')) {
    return 'HH:mm' as DatePattern;
  }
  if (pattern.includes('h:mm')) {
    return 'h:mm' as DatePattern;
  }

  return 'HH:mm' as DatePattern;
};

/**
 * 날짜 구성 요소를 추출합니다.
 * @param date Date 객체
 * @returns 날짜 구성 요소 객체
 */
const extractDateComponents = (date: Date) => {
  const yyyy = date.getFullYear();
  const yy = yyyy % 100;
  const mm = String(date.getMonth() + 1).padStart(2, '0');
  const dd = String(date.getDate()).padStart(2, '0');
  const hh = String(date.getHours()).padStart(2, '0');
  const min = String(date.getMinutes()).padStart(2, '0');
  const h12 = date.getHours() % 12 || 12; // 12시간제

  return { yyyy, yy, mm, dd, hh, min, h12 };
};

/**
 * 날짜를 지정된 패턴과 로케일에 맞게 포맷팅합니다.
 * @param date Date 객체
 * @param pattern 날짜 형식 패턴 (DatePattern 참고)
 * @param locale 로케일
 * @param dayOfWeekFormat 요일 형식
 * @returns 포맷팅된 날짜 문자열
 */
const applyDatePattern = (
  date: Date,
  pattern: DatePattern,
  locale: LocaleOption,
  dayOfWeekFormat: DayOfWeekFormat
) => {
  const { yyyy, yy, mm, dd, hh, min, h12 } = extractDateComponents(date);

  const ampm = getLocaleParts(date, locale);
  const effectiveDayFormat =
    pattern.includes('E') && dayOfWeekFormat === 'none' ? 'short' : dayOfWeekFormat;
  const dayLabel = getLocalizedDayName(date, locale, effectiveDayFormat);

  const result = pattern
    .replace(/YYYY/g, String(yyyy))
    .replace(/YY/g, String(yy))
    .replace(/MM/g, String(mm))
    .replace(/DD/g, String(dd))
    .replace(/HH/g, String(hh))
    .replace(/mm/g, String(min))
    .replace(/A/g, ampm)
    .replace(/h/g, String(h12))
    .replace(/E/g, dayLabel.trim());

  return result;
};

/**
 * 요일 이름을 로케일에 맞게 가져옵니다.
 * @param date Date 객체
 * @param locale 로케일
 * @param format 요일 형식
 * @returns 포맷팅된 요일 문자열
 */
const getLocalizedDayName = (date: Date, locale: string, format: DayOfWeekFormat = 'none') => {
  if (format === 'none') return '';

  const fullDayName = new Intl.DateTimeFormat(locale, {
    weekday: 'long',
  }).format(date);
  const shortDayName = new Intl.DateTimeFormat(locale, {
    weekday: 'short',
  }).format(date);

  if (format === 'short') {
    return `${shortDayName}`;
  }

  if (format === 'shortParen') {
    return `(${shortDayName})`;
  }
  if (format === 'long') {
    return `${fullDayName}`;
  }

  return '';
};

/**
 * 오전/오후 문자열을 로케일에 맞게 가져옵니다.
 * @param date Date 객체
 * @param locale 로케일
 * @returns 오전/오후 문자열 (locale에 따라 다름)
 */
const getLocaleParts = (date: Date, locale: LocaleOption) => {
  const fmt = new Intl.DateTimeFormat(locale, {
    weekday: 'short',
    hour: 'numeric',
    minute: 'numeric',
    hour12: true,
  }).formatToParts(date);

  return fmt.find((p) => p.type === 'dayPeriod')?.value ?? '';
};
