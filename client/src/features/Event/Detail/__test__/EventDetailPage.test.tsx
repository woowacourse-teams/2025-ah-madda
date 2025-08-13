import { ThemeProvider } from '@emotion/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, it, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '@/api/fetcher';
import { theme } from '@/shared/styles/theme';

import { EventDetailPage } from '../pages/EventDetailPage';

vi.mock('@/api/fetcher', () => ({
  fetcher: { get: vi.fn() },
}));

const createClient = () =>
  new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

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
    { questionId: 1, questionText: '자기소개를 해주세요', isRequired: true, orderIndex: 0 },
  ],
};

const mockFetcher = fetcher as Mocked<typeof fetcher>;

describe('EventDetailPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  const renderPage = () => {
    const client = createClient();
    return render(
      <QueryClientProvider client={client}>
        <ThemeProvider theme={theme}>
          <MemoryRouter initialEntries={['/event/123']}>
            <Routes>
              <Route path="/event/:eventId" element={<EventDetailPage />} />
            </Routes>
          </MemoryRouter>
        </ThemeProvider>
      </QueryClientProvider>
    );
  };

  it('기본 정보가 올바르게 렌더링된다', async () => {
    mockFetcher.get.mockResolvedValue(mockEventDetail);

    renderPage();

    expect(await screen.findByText('테스트 이벤트 제목')).toBeInTheDocument();
    expect(screen.getByText('이벤트 설명입니다.')).toBeInTheDocument();
    expect(screen.getByText(/홍길동/)).toBeInTheDocument();
    expect(screen.getByText('서울시 종로구')).toBeInTheDocument();
  });

  it('참가 현황이 표시된다', async () => {
    mockFetcher.get.mockResolvedValue(mockEventDetail);

    renderPage();
    screen.debug();
    expect(await screen.findByText('3 / 10')).toBeInTheDocument();
  });

  it('질문 목록이 표시된다', async () => {
    mockFetcher.get.mockResolvedValue(mockEventDetail);

    renderPage();

    expect(await screen.findByText('자기소개를 해주세요')).toBeInTheDocument();
  });
});
