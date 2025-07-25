import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '../../../../api/fetcher';
import { EventDetailPage } from '../pages/EventDetailPage';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const mockEventDetail = {
  eventId: 123,
  title: '테스트 이벤트 제목',
  description: '이벤트 설명입니다.',
  place: '서울시 종로구',
  organizerName: '홍길동',
  registrationStart: '2025-07-01T10:00:00Z',
  registrationEnd: '2025-07-05T23:59:59Z',
  eventStart: '2025-07-10T14:00:00Z',
  eventEnd: '2025-07-10T16:00:00Z',
  currentGuestCount: 3,
  maxCapacity: 10,
  questions: [
    {
      questionId: 1,
      questionText: '자기소개를 해주세요',
      isRequired: true,
      orderIndex: 0,
    },
  ],
};

const TestWrapper = ({ initialRoute = '/event/detail/123' }: { initialRoute?: string }) => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return (
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={[initialRoute]}>
        <Routes>
          <Route path="/event/detail/:eventId" element={<EventDetailPage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  );
};

describe('EventDetailPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('events/123')) {
        return Promise.resolve(mockEventDetail);
      }
      return Promise.reject(new Error(`Unknown API call: ${url}`));
    });
  });

  test('이벤트 제목, 설명, 주최자, 장소가 올바르게 렌더링된다', async () => {
    render(<TestWrapper />);

    await waitFor(() => {
      expect(screen.getByText('테스트 이벤트 제목')).toBeInTheDocument();
      expect(screen.getByText('이벤트 설명입니다.')).toBeInTheDocument();
      expect(screen.getByText(/홍길동/)).toBeInTheDocument();
      expect(screen.getByText('서울시 종로구')).toBeInTheDocument();
    });
  });

  test('참가 현황과 질문 정보가 렌더링된다', async () => {
    render(<TestWrapper />);

    await waitFor(() => {
      expect(screen.getByText('3 / 10명')).toBeInTheDocument();
      expect(screen.getByText('자기소개를 해주세요')).toBeInTheDocument();
    });
  });
});
