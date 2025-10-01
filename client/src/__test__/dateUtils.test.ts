import { formatDate } from '@/shared/utils/dateUtils';

describe('날짜 유틸리티 함수 테스트', () => {
  test('start만 있는 경우 단일 날짜 포맷팅', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T14:30:00'),
        options: {
          pattern: 'YYYY.MM.DD',
          dayOfWeekFormat: 'short',
          locale: 'ko',
        },
      })
    ).toBe('2025.01.15');

    expect(
      formatDate({
        start: '2025-01-15T14:30:00',
        options: {
          pattern: 'YYYY-MM-DD',
          dayOfWeekFormat: 'short',
          locale: 'ko',
        },
      })
    ).toBe('2025-01-15');

    expect(
      formatDate({
        start: '2025-01-15T14:30:00',
        options: {
          pattern: 'YYYY년 MM월 DD일',
          dayOfWeekFormat: 'short',
          locale: 'ko',
        },
      })
    ).toBe('2025년 01월 15일');
  });

  test('start와 end가 있는 경우 날짜 범위 포맷팅', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        end: new Date('2025-01-17T18:00:00'),
        options: {
          pattern: 'YYYY.MM.DD HH:mm',
          rangeSeparator: '~',
        },
      })
    ).toBe('2025.01.15 10:00 ~ 2025.01.17 18:00');
  });

  test('rangeSeparator 옵션이 적용되는지 확인', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        end: new Date('2025-01-17T18:00:00'),
        options: {
          pattern: 'YYYY.MM.DD HH:mm',
          rangeSeparator: '-',
        },
      })
    ).toBe('2025.01.15 10:00 - 2025.01.17 18:00');
  });

  test('dayOfWeekFormat에 따른 요일 포맷팅', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        end: new Date('2025-01-17T18:00:00'),
        options: {
          pattern: 'YYYY.MM.DD (E) HH:mm',
          dayOfWeekFormat: 'short',
          locale: 'ko',
        },
      })
    ).toBe('2025.01.15 (수) 10:00 ~ 2025.01.17 (금) 18:00');

    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        end: new Date('2025-01-17T18:00:00'),
        options: {
          pattern: 'YYYY.MM.DD (E) HH:mm',
          dayOfWeekFormat: 'long',
          locale: 'ko',
        },
      })
    ).toBe('2025.01.15 (수요일) 10:00 ~ 2025.01.17 (금요일) 18:00');

    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        end: new Date('2025-01-17T18:00:00'),
        options: {
          pattern: 'YYYY.MM.DD E HH:mm',
          dayOfWeekFormat: 'shortParen',
          locale: 'ko',
        },
      })
    ).toBe('2025.01.15 (수) 10:00 ~ 2025.01.17 (금) 18:00');
  });

  test('locale 옵션에 따른 언어 설정', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        options: {
          pattern: 'YYYY.MM.DD (E) HH:mm',
          dayOfWeekFormat: 'short',
          locale: 'en',
        },
      })
    ).toBe('2025.01.15 (Wed) 10:00');
  });

  test('smartRange 옵션이 같은 날짜 범위를 간결하게 표시한다', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        end: new Date('2025-01-15T12:00:00'),
        options: {
          pattern: 'YYYY.MM.DD HH:mm',
          smartRange: true,
        },
      })
    ).toBe('2025.01.15 10:00 ~ 12:00');
  });

  test('smartRange 옵션이 다른 날짜에는 적용되지 않는다', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T10:00:00'),
        end: new Date('2025-01-16T12:00:00'),
        options: {
          pattern: 'YYYY.MM.DD HH:mm',
          smartRange: true,
        },
      })
    ).toBe('2025.01.15 10:00 ~ 2025.01.16 12:00');
  });

  test('formatDate 함수가 유효하지 않은 날짜를 처리한다', () => {
    expect(() =>
      formatDate({
        start: 'invalid-date',
        options: { pattern: 'YYYY-MM-DD' },
      })
    ).toThrow('Invalid start date');

    expect(() =>
      formatDate({
        start: '2025-01-15',
        end: 'invalid-date',
        options: { pattern: 'YYYY-MM-DD' },
      })
    ).toThrow('Invalid end date');
  });

  test('formatDate 함수가 올바른 형식의 날짜 문자열을 반환한다', () => {
    expect(
      formatDate({
        start: new Date('2025-01-15T14:30:00'),
        options: {
          pattern: 'YYYY.MM.DD (E) HH:mm',
          dayOfWeekFormat: 'short',
          locale: 'ko',
        },
      })
    ).toBe('2025.01.15 (수) 14:30');
  });
});
