import React from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { renderHook, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '../api/fetcher';
import { useAddAlarm, postAlarm } from '../api/mutations/useAddAlarm';

vi.mock('../../fetcher', () => ({
  fetcher: {
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const testContainer = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  const TestWrapper = ({ children }: { children: React.ReactNode }) => (
    <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
  );

  TestWrapper.displayName = 'TestWrapper';

  return TestWrapper;
};

describe('useAddAlarm', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('엣지 케이스 입력값 처리', () => {
    test('빈 문자열 알람으로는 전송할 수 없다', async () => {
      const eventId = 400;
      const emptyContent = '';

      mockFetcher.post.mockResolvedValue(undefined);

      const wrapper = testContainer();
      const { result } = renderHook(() => useAddAlarm({ eventId }), { wrapper });

      result.current.mutate(emptyContent);

      await waitFor(() => {
        expect(mockFetcher.post).not.toHaveBeenCalled();
      });
    });
  });

  describe('postAlarm 함수', () => {
    test('올바른 API 경로와 데이터로 POST 요청을 보낸다', async () => {
      const eventId = 123;
      const content = '직접 호출 테스트';

      mockFetcher.post.mockResolvedValue(undefined);

      await postAlarm(eventId, content);

      expect(mockFetcher.post).toHaveBeenCalledWith(`events/${eventId}/notify-non-guests`, {
        json: { content },
      });
    });
  });
});
