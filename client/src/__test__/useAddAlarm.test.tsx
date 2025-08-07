import { renderHook, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '@/api/fetcher';
import { useAddAlarm, postAlarm } from '@/api/mutations/useAddAlarm';

import { createTestQueryClient } from './createTestQueryClient';
import { QueryClientProviderWrapper } from './customRender';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

describe('useAddAlarm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('엣지 케이스 입력값 처리', () => {
    test('빈 문자열 알람으로는 전송할 수 없다', async () => {
      const eventId = 400;
      const emptyContent = '';

      mockFetcher.post.mockResolvedValue(undefined);

      const { result } = renderHook(() => useAddAlarm({ eventId }), {
        wrapper: ({ children }) => (
          <QueryClientProviderWrapper queryClient={createTestQueryClient()}>
            {children}
          </QueryClientProviderWrapper>
        ),
      });

      result.current.mutate({
        content: emptyContent,
        organizationMemberIds: [],
      });

      await waitFor(() => {
        expect(mockFetcher.post).not.toHaveBeenCalled();
      });
    });
  });

  describe('postAlarm 함수', () => {
    test('올바른 API 경로와 데이터로 POST 요청을 보낸다', async () => {
      const eventId = 123;
      const notificationData = {
        content: '직접 호출 테스트',
        organizationMemberIds: [1, 2, 3],
      };

      mockFetcher.post.mockResolvedValue(undefined);

      await postAlarm(eventId, notificationData);

      expect(mockFetcher.post).toHaveBeenCalledWith(
        `events/${eventId}/notify-organization-members`,
        notificationData
      );
    });
  });
});
