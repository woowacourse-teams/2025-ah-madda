import { sum } from '../features/Home/utils/sum';

test('should return 3', () => {
  expect(sum(1, 2)).toBe(3);
});
