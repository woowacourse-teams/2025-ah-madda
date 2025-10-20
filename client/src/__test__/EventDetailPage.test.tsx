import { Suspense } from 'react';

import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach } from 'vitest';

import { fetcher } from '@/api/fetcher';
import { EventDetailPage } from '@/features/Event/Detail/pages/EventDetailPage';

import { RouterWithQueryClient } from './customRender';
import { mockEventDetail } from './mocks/event';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
  },
}));

vi.mock('@/api/auth', () => ({
  isAuthenticated: () => true,
}));
const mockFetcher = vi.mocked(fetcher);

const setupMockResponses = ({
  eventDetail = mockEventDetail,
  isOrganizer = false,
  isGuest = false,
}: {
  eventDetail?: typeof mockEventDetail;
  isOrganizer?: boolean;
  isGuest?: boolean;
}) => {
  mockFetcher.get.mockImplementation((url: string) => {
    if (url.endsWith('/organizer-status')) return Promise.resolve({ isOrganizer });
    if (url.endsWith('/guest-status')) return Promise.resolve({ isGuest });
    if (url.endsWith('/notification/opt-out')) return Promise.resolve({ optedOut: false });
    if (url.includes('organizations/events/123')) return Promise.resolve(eventDetail);
    return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
  });
};

describe('EventDetailPage', () => {
  beforeEach(() => {
    mockFetcher.get.mockReset();
  });

  const renderEventDetailPage = () => {
    return render(
      <RouterWithQueryClient
        initialRoute="/event/123"
        routes={[
          {
            path: '/event/:eventId',
            element: (
              <Suspense fallback={<div>Loading...</div>}>
                <EventDetailPage />
              </Suspense>
            ),
          },
        ]}
      />
    );
  };

  describe('이벤트 상세 페이지 렌더링 테스트', () => {
    test('기본 정보가 올바르게 렌더링된다', async () => {
      setupMockResponses({
        eventDetail: mockEventDetail,
        isOrganizer: false,
        isGuest: false,
      });
      renderEventDetailPage();

      await waitFor(() => {
        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
      });

      expect(screen.getByText('테스트 이벤트 설명')).toBeInTheDocument();
      expect(screen.getByText(/홍길동/)).toBeInTheDocument();
      expect(screen.getByText('서울시 강남구')).toBeInTheDocument();
    });

    test('참가 현황이 표시된다', async () => {
      setupMockResponses({
        eventDetail: mockEventDetail,
        isOrganizer: false,
        isGuest: false,
      });

      renderEventDetailPage();

      await waitFor(() => {
        expect(screen.getByText('5 / 20')).toBeInTheDocument();
      });
    });

    test('질문 목록이 표시된다', async () => {
      setupMockResponses({
        eventDetail: mockEventDetail,
        isOrganizer: false,
        isGuest: false,
      });

      renderEventDetailPage();

      await waitFor(() => {
        expect(screen.getByText('참석 동기를 알려주세요')).toBeInTheDocument();
      });
    });
  });

  describe('이벤트 종료 여부와 신청 여부에 따른 UI 렌더링 표시', () => {
    test('신청 마감이 지나지 않았고 참가 신청을 하지 않았다면, "신청하기" 버튼을 보여준다.', async () => {
      setupMockResponses({
        eventDetail: { ...mockEventDetail, registrationEnd: '2099-12-31T23:59:59' },
        isOrganizer: false,
        isGuest: false,
      });

      renderEventDetailPage();

      expect(await screen.findByRole('button', { name: '신청 하기' })).toBeInTheDocument();
    });

    test('이벤트가 시작되었고 참가 신청을 했다면, "신청 취소" 버튼을 보여준다.', async () => {
      setupMockResponses({
        eventDetail: { ...mockEventDetail, registrationEnd: '2099-12-31T23:59:59' },
        isOrganizer: false,
        isGuest: true,
      });

      renderEventDetailPage();

      await waitFor(() => {
        expect(screen.getByText('신청 취소')).toBeInTheDocument();
      });
    });

    test('참가 이력이 없고 이벤트가 종료되면, "신청 마감" 버튼이 표시된다.', async () => {
      setupMockResponses({
        eventDetail: { ...mockEventDetail, registrationEnd: '2020-12-31T23:59:59' },
        isOrganizer: false,
        isGuest: false,
      });

      renderEventDetailPage();

      await waitFor(() => {
        expect(screen.getByText('신청 마감')).toBeInTheDocument();
      });
    });

    test('참가 이력이 있고 이벤트가 종료되면, "신청 완료" 버튼이 표시된다.', async () => {
      setupMockResponses({
        eventDetail: { ...mockEventDetail, registrationEnd: '2020-12-31T23:59:59' },
        isOrganizer: false,
        isGuest: true,
      });

      renderEventDetailPage();

      await waitFor(() => {
        expect(screen.getByText('신청 완료')).toBeInTheDocument();
      });
    });
  });
});
