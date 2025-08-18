import { describe, expect } from 'vitest';

import { formatDateTime } from '../formatDateTime';

describe('formatDateTime', () => {
  test('같은 날 다른 시간대의 이벤트를 올바르게 포맷한다', () => {
    const eventStart = '2024-12-25T10:00:00';
    const eventEnd = '2024-12-25T12:00:00';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('12.25 (수) 10:00 ~ 12:00');
  });

  test('한국 시간으로 자정을 올바르게 처리한다', () => {
    const eventStart = '2024-06-01T15:00:00';
    const eventEnd = '2024-06-01T17:00:00';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('6.1 (토) 15:00 ~ 17:00');
  });

  test('분단위를 포함하여 시간을 표시한다', () => {
    const eventStart = '2024-04-10T01:45:30';
    const eventEnd = '2024-04-10T03:15:45';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('4.10 (수) 01:45 ~ 03:15');
  });

  test('같은 시간일 때도 올바르게 처리한다', () => {
    const eventStart = '2024-05-20T04:00:00';
    const eventEnd = '2024-05-20T04:00:00';

    const result = formatDateTime(eventStart, eventEnd);

    expect(result).toBe('5.20 (월) 04:00 ~ 04:00');
  });

  describe('에러 케이스', () => {
    test('null 입력에 대해 적절한 메시지를 반환한다', () => {
      const result = formatDateTime(null, '2024-12-25T12:00:00');
      expect(result).toBe('날짜 정보가 없습니다');
    });

    test('undefined 입력에 대해 적절한 메시지를 반환한다', () => {
      const result = formatDateTime('2024-12-25T10:00:00', undefined);
      expect(result).toBe('날짜 정보가 없습니다');
    });

    test('빈 문자열 입력에 대해 적절한 메시지를 반환한다', () => {
      const result = formatDateTime('', '2024-12-25T12:00:00');
      expect(result).toBe('날짜 정보가 없습니다');
    });

    test('잘못된 날짜 형식에 대해 NaN을 포함한 결과를 반환한다', () => {
      const result = formatDateTime('2024-13-45T12:00:00', '2024-12-25T12:00:00');
      expect(result).toContain('NaN');
    });

    test('잘못된 시간 형식에 대해 NaN을 포함한 결과를 반환한다', () => {
      const result = formatDateTime('2024-12-25T10:00:00', '2024-12-28T25:70:99');
      expect(result).toContain('NaN');
    });

    test('문자열이지만 날짜가 아닌 값에 대해 NaN을 포함한 결과를 반환한다', () => {
      const result = formatDateTime('hello world', '2024-12-25T12:00:00');
      expect(result).toContain('NaN');
    });

    test('두 입력 모두 잘못된 경우에도 NaN을 포함한 결과를 반환한다', () => {
      const result = formatDateTime('2024.13-45T12:00:00', '2024.12-28T25:70:99');
      expect(result).toContain('NaN');
    });

    test('null과 undefined가 모두 입력된 경우 적절한 메시지를 반환한다', () => {
      const result = formatDateTime(null, undefined);
      expect(result).toBe('날짜 정보가 없습니다');
    });
  });
});
