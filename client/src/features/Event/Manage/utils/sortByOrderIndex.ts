import type { GuestAnswerAPIResponse } from '@/api/types/my';

export const byOrderIndexAsc = (a: GuestAnswerAPIResponse, b: GuestAnswerAPIResponse) =>
  (a.orderIndex ?? Number.MAX_SAFE_INTEGER) - (b.orderIndex ?? Number.MAX_SAFE_INTEGER);
