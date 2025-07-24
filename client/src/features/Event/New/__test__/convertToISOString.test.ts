import { convertToISOString } from '../utils/convertToISOString';

describe('convertToISOString', () => {
  it('올바른 날짜와 시간을 ISO 문자열로 변환한다', () => {
    const input = '2025.07.30 13:00';
    const result = convertToISOString(input);
    expect(result).toBe('2025-07-30T13:00:00.000Z');
  });

  it('빈 문자열을 입력하면 빈 문자열을 반환한다', () => {
    expect(convertToISOString('')).toBe('');
  });

  it('잘못된 형식의 문자열 (날짜 누락)을 입력하면 빈 문자열을 반환한다', () => {
    expect(convertToISOString('13:00')).toBe('');
  });

  it('잘못된 형식의 문자열 (시간 누락)을 입력하면 빈 문자열을 반환한다', () => {
    expect(convertToISOString('2025.07.30')).toBe('');
  });
});
