/**
 * 날짜 유틸리티 함수들
 */
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
};

type DateRangeFormatInput = {
  /**
   * 시작 날짜
   * @type {Date}
   */
  start: Date | string;
  /**
   * 종료 날짜
   * @type {Date}
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
 * @param {option} [params.options] - 포맷 옵션
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
 */
export const formatDate = ({ start, end, options = {} }: DateRangeFormatInput): string => {
  const {
    pattern = 'YYYY.MM.DD HH:mm',
    dayOfWeekFormat = 'none',
    rangeSeparator = '~',
    locale = 'ko',
  } = options;

  const startDate = typeof start === 'string' ? new Date(start) : start;
  const endDate = end ? (typeof end === 'string' ? new Date(end) : end) : null;

  const startStr = applyDatePattern(startDate, pattern, locale, dayOfWeekFormat);
  const endStr = endDate ? applyDatePattern(endDate, pattern, locale, dayOfWeekFormat) : null;

  return endStr ? `${startStr} ${rangeSeparator} ${endStr}` : startStr;
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
  const dayLabel = getLocalizedDayName(date, locale, dayOfWeekFormat);

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
    return ` ${shortDayName}`;
  }

  if (format === 'shortParen') {
    return ` ${shortDayName}`;
  }
  if (format === 'long') {
    return ` ${fullDayName}`;
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
