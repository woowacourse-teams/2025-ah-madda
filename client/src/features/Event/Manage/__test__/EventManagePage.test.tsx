import React from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Routes, Route } from 'react-router-dom';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '@/api/fetcher';

import { MyEventPage } from '../../My/pages/MyEventPage';
import { EventManagePage } from '../pages/EventManagePage';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const mockHostEvents = [
  {
    eventId: 123,
    title: '테스트 이벤트',
    description: '테스트 이벤트 설명',
    organizerName: '홍길동',
    registrationEnd: '2024-12-31T23:59:59Z',
    eventStart: '2024-01-15T10:00:00Z',
    eventEnd: '2024-01-15T12:00:00Z',
    place: '서울시 강남구',
    currentGuestCount: 5,
    maxCapacity: 20,
  },
  {
    eventId: 456,
    title: '두 번째 이벤트',
    description: '두 번째 이벤트 설명',
    organizerName: '김철수',
    registrationEnd: '2024-12-25T23:59:59Z',
    eventStart: '2024-01-20T14:00:00Z',
    eventEnd: '2024-01-20T16:00:00Z',
    place: '부산시 해운대구',
    currentGuestCount: 10,
    maxCapacity: 30,
  },
];

const mockEventDetail = {
  eventId: 123,
  title: '테스트 이벤트',
  description: '테스트 이벤트 설명',
  organizerName: '홍길동',
  registrationStart: '2024-01-01T00:00:00Z',
  registrationEnd: '2024-12-31T23:59:59Z',
  eventStart: '2024-01-15T10:00:00Z',
  eventEnd: '2024-01-15T12:00:00Z',
  place: '서울시 강남구',
  currentGuestCount: 5,
  maxCapacity: 20,
  questions: [
    {
      questionId: 1,
      questionText: '참석 동기를 알려주세요',
      isRequired: true,
      orderIndex: 0,
    },
  ],
};

const mockGuests = [
  {
    guestId: 1,
    organizationMemberId: 1,
    nickname: '참석자1',
  },
  {
    guestId: 2,
    organizationMemberId: 2,
    nickname: '참석자2',
  },
];

const mockNonGuests = [
  {
    organizationMemberId: 3,
    nickname: '미참석자1',
  },
];

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

describe('MyEventPage에서 EventInfoSection으로 이동 후 렌더링 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();

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
      return Promise.reject(new Error(`url 확인: ${url}`));
    });
  });

  describe('MyEventPage 렌더링', () => {
    test('MyEventPage가 정상적으로 렌더링된다', async () => {
      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('진행 중인 이벤트')).toBeInTheDocument();
      });
    });

    test('주최 이벤트 목록이 표시된다', async () => {
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

  describe('EventInfoSection 렌더링', () => {
    test('EventInfoSection이 올바른 이벤트 정보를 표시한다', async () => {
      render(<TestContainer initialRoute="/event/manage/123" />);

      await waitFor(() => {
        expect(screen.getByText('이벤트 정보')).toBeInTheDocument();
        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
        expect(screen.getByText('테스트 이벤트 설명')).toBeInTheDocument();
      });
    });

    test('주최자 정보가 올바르게 표시된다', async () => {
      render(<TestContainer initialRoute="/event/manage/123" />);

      await waitFor(() => {
        expect(screen.getByText('주최자: 홍길동')).toBeInTheDocument();
      });
    });

    test('장소 정보가 표시된다', async () => {
      render(<TestContainer initialRoute="/event/manage/123" />);

      await waitFor(() => {
        expect(screen.getByText('서울시 강남구')).toBeInTheDocument();
      });
    });

    test('참가 현황이 올바르게 표시된다', async () => {
      render(<TestContainer initialRoute="/event/manage/123" />);

      await waitFor(() => {
        expect(screen.getByText('참가 현황')).toBeInTheDocument();
        expect(screen.getByText('5/20명')).toBeInTheDocument();
      });
    });

    test('ProgressBar가 올바른 값으로 렌더링된다', async () => {
      render(<TestContainer initialRoute="/event/manage/123" />);

      await waitFor(() => {
        const progressBar = screen.getByLabelText(/Progress: 25%/);
        expect(progressBar).toBeInTheDocument();
      });
    });
  });

  describe('엣지 케이스 처리', () => {
    test('빈 이벤트 목록일 때 적절한 메시지를 표시한다', async () => {
      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organization-members/1/events/owned')) {
          return Promise.resolve([]);
        }
        if (url.includes('organization-members/1/events/participated')) {
          return Promise.resolve([]);
        }
        return Promise.resolve([]);
      });

      render(<TestContainer />);

      await waitFor(() => {
        expect(screen.getByText('주최한 이벤트가 없습니다.')).toBeInTheDocument();
      });
    });
  });
});
