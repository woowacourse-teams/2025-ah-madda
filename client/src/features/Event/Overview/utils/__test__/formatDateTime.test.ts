import { describe, expect } from 'vitest';

import { formatDateTime } from '../formatDateTime';

describe('formatDateTime', () => {
  test('같은 날 다른 시간대의 이벤트를 올바르게 포맷한다', () => {
    const eventStart = '2024-12-25T10:00:00';
    const eventEnd = '2024-12-25T12:00:00';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('12.25 (수) 10시 ~ 12시');
  });

  test('한국 시간으로 자정을 올바르게 처리한다', () => {
    const eventStart = '2024-06-01T15:00:00';
    const eventEnd = '2024-06-01T17:00:00';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('6.1 (토) 15시 ~ 17시');
  });

  test('분단위는 무시하고 시간만 표시한다', () => {
    const eventStart = '2024-04-10T01:45:30';
    const eventEnd = '2024-04-10T03:15:45';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('4.10 (수) 1시 ~ 3시');
  });

  test('같은 시간일 때도 올바르게 처리한다', () => {
    const eventStart = '2024-05-20T04:00:00';
    const eventEnd = '2024-05-20T04:00:00';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('5.20 (월) 4시 ~ 4시');
  });
});
