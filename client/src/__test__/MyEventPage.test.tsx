import React from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '@/api/fetcher';
import { EventManagePage } from '@/features/Event/Manage/pages/EventManagePage';
import { MyEventPage } from '@/features/Event/My/pages/MyEventPage';
import { mockEventDetail, mockGuests, mockHostEvents, mockNonGuests } from '@/shared/mocks';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const TestContainer = ({ initialRoute = '/event/my' }: { initialRoute?: string }) => {
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
          <Route path="/event/my" element={<MyEventPage />} />
          <Route path="/event/manage/:eventId" element={<EventManagePage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  );
};

describe('MyEventPage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('MyEventPage 렌더링', () => {
    test('MyEventPage가 정상적으로 렌더링된다', async () => {
      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organization-members/1/events/participated')) {
          return Promise.resolve([]);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('진행 중인 이벤트')).toBeInTheDocument();
      });
    });

    test('주최 이벤트 목록이 표시된다', async () => {
      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organization-members/1/events/owned')) {
          return Promise.resolve(mockHostEvents);
        }
        if (url.includes('organization-members/1/events/participated')) {
          return Promise.resolve([]);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
        expect(screen.getByText('테스트 이벤트 설명')).toBeInTheDocument();
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      await waitFor(() => {
        expect(screen.getByText('두 번째 이벤트')).toBeInTheDocument();
        expect(screen.getByText('두 번째 이벤트 설명')).toBeInTheDocument();
        expect(screen.getByText('김철수')).toBeInTheDocument();
      });
    });

    test('참여 현황이 올바르게 표시된다', async () => {
      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organization-members/1/events/owned')) {
          return Promise.resolve(mockHostEvents);
        }
        if (url.includes('organization-members/1/events/participated')) {
          return Promise.resolve([]);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('5/20명')).toBeInTheDocument();
        expect(screen.getByText('10/30명')).toBeInTheDocument();
      });
    });
  });

  describe('EventCard 클릭 시 로직 테스트', () => {
    test('EventCard 클릭 시 본인이 주최한 이벤트 관리 페이지의 api를 호출한다', async () => {
      const user = userEvent.setup();

      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organization-members/1/events/owned')) {
          return Promise.resolve(mockHostEvents);
        }
        if (url.includes('organization-members/1/events/participated')) {
          return Promise.resolve([]);
        }
        if (url.includes('organizations/events/123')) {
          return Promise.resolve(mockEventDetail);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
      });

      const eventCard = screen.getByText('테스트 이벤트').closest('section');
      expect(eventCard).toBeInTheDocument();

      await user.click(eventCard!);

      await waitFor(
        () => {
          expect(mockFetcher.get).toHaveBeenCalledWith('organizations/events/123');
        },
        { timeout: 3000 }
      );
    });

    test('클릭한 이벤트의 참여자/미신청자 조회 API가 호출된다', async () => {
      const user = userEvent.setup();

      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organization-members/1/events/owned')) {
          return Promise.resolve(mockHostEvents);
        }
        if (url.includes('organization-members/1/events/participated')) {
          return Promise.resolve([]);
        }
        if (url.includes('organizations/events/123')) {
          return Promise.resolve(mockEventDetail);
        }
        if (url.includes('events/123/guests')) {
          return Promise.resolve(mockGuests);
        }
        if (url.includes('events/123/non-guests')) {
          return Promise.resolve(mockNonGuests);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
      });

      const eventCard = screen.getByText('테스트 이벤트').closest('section');
      await user.click(eventCard!);

      await waitFor(() => {
        expect(mockFetcher.get).toHaveBeenCalledWith('events/123/guests');
        expect(mockFetcher.get).toHaveBeenCalledWith('events/123/non-guests');
      });
    });
  });
});
