import React from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '@/api/fetcher';
import { EventManagePage } from '@/features/Event/Manage/pages/EventManagePage';
import { mockEventDetail } from '@/shared/mocks';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const TestContainer = ({ initialRoute = '/event/manage/123' }: { initialRoute?: string }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false, staleTime: 0 },
      mutations: { retry: false },
    },
  });

  return (
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialRoute]}>
        <Routes>
          <Route path="/event/manage/:eventId" element={<EventManagePage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  );
};

describe('EventManagePage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('organizations/events/123')) {
        return Promise.resolve(mockEventDetail);
      }
      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });
  });

  describe('EventInfoSection 렌더링', () => {
    test('EventInfoSection이 올바른 이벤트 정보를 표시한다', async () => {
      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('이벤트 정보')).toBeInTheDocument();
        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
        expect(screen.getByText('테스트 이벤트 설명')).toBeInTheDocument();
      });
    });

    test('주최자 정보가 올바르게 표시된다', async () => {
      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('주최자: 홍길동')).toBeInTheDocument();
      });
    });

    test('장소 정보가 표시된다', async () => {
      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('서울시 강남구')).toBeInTheDocument();
      });
    });
  });
});
