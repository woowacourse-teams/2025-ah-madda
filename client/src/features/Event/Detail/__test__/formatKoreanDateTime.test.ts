import { describe, expect, test } from 'vitest';

import { formatKoreanDateTime } from '../utils/formatKoreanDateTime';

describe('formatKoreanDateTime', () => {
  test('오전 시간대를 올바르게 포맷한다', () => {
    const input = '2025-07-01T06:30:00+09:00';
    const output = formatKoreanDateTime(input);
    expect(output).toBe('2025년 7월 1일 화요일 오전 06:30');
  });

  test('오후 시간대를 올바르게 포맷한다', () => {
    const input = '2025-07-01T15:05:00+09:00';
    const output = formatKoreanDateTime(input);
    expect(output).toBe('2025년 7월 1일 화요일 오후 03:05');
  });

  test('자정은 오전 12시로 포맷된다', () => {
    const input = '2025-07-01T00:00:00+09:00';
    const output = formatKoreanDateTime(input);
    expect(output).toBe('2025년 7월 1일 화요일 오전 12:00');
  });

  test('정오는 오후 12시로 포맷된다', () => {
    const input = '2025-07-01T12:00:00+09:00';
    const output = formatKoreanDateTime(input);
    expect(output).toBe('2025년 7월 1일 화요일 오후 12:00');
  });

  test('요일이 올바르게 계산된다 (예: 일요일)', () => {
    const input = '2025-06-29T08:00:00+09:00';
    const output = formatKoreanDateTime(input);
    expect(output).toBe('2025년 6월 29일 일요일 오전 08:00');
  });
});
