import { fireEvent, render, screen, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { mockEventDetail } from '@/__test__/mocks/event';
import { fetcher } from '@/api/fetcher';
import { EventManagePage } from '@/features/Event/Manage/pages/EventManagePage';

import { RouterWithQueryClient } from './customRender';

const mockMutate = vi.fn();
vi.mock('@/api/mutations/useCloseEventRegistration', () => ({
  useCloseEventRegistration: () => ({
    mutate: mockMutate,
  }),
}));

vi.mock('@tanstack/react-query', async () => {
  const actual = await vi.importActual('@tanstack/react-query');
  return {
    ...actual,
    useSuspenseQueries: () => [
      {
        data: { name: '홍길동', picture: '' },
        isLoading: false,
        isError: false,
      },
      {
        data: [],
        isLoading: false,
        isError: false,
      },
    ],
  };
});

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const mockEventDetailApiResponse = (data: typeof mockEventDetail) => {
  mockFetcher.get.mockImplementation((url: string) => {
    if (url.includes('organizations/events/123')) {
      return Promise.resolve(data);
    }
    return Promise.reject(new Error(`API 호출 실패: ${url}`));
  });
};

describe('EventManagePage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockEventDetailApiResponse(mockEventDetail);
  });

  const renderEventManagePage = () => {
    return render(
      <RouterWithQueryClient
        initialRoute="/event/manage/123"
        routes={[{ path: '/event/manage/:eventId', element: <EventManagePage /> }]}
      />
    );
  };

  const setupMockConfirm = (returnValue: boolean) => {
    const mockConfirm = vi.spyOn(window, 'confirm').mockReturnValue(returnValue);
    return mockConfirm;
  };

  describe('EventInfoSection 렌더링', () => {
    test('EventInfoSection이 올바른 이벤트 정보를 표시한다', async () => {
      renderEventManagePage();

      await waitFor(() => {
        expect(screen.getByRole('tab', { name: '이벤트 정보' })).toBeInTheDocument();
      });

      expect(await screen.findByText('테스트 이벤트')).toBeInTheDocument();
      expect(await screen.findByText('테스트 이벤트 설명')).toBeInTheDocument();
    });

    test('주최자 정보가 올바르게 표시된다', async () => {
      renderEventManagePage();

      expect(await screen.findByText('홍길동')).toBeInTheDocument();
    });

    test('장소 정보가 표시된다', async () => {
      renderEventManagePage();

      expect(await screen.findByText('서울시 강남구')).toBeInTheDocument();
    });
  });

  describe('이벤트 마감 기능 테스트', () => {
    test('이벤트 마감 버튼이 렌더링된다', async () => {
      renderEventManagePage();

      expect(await screen.findByText('마감하기')).toBeInTheDocument();
    });

    test('이벤트 마감 버튼을 클릭하고 취소하면 마감 API가 호출되지 않는다', async () => {
      setupMockConfirm(false);
      renderEventManagePage();

      await waitFor(() => {
        expect(screen.getByText('마감하기')).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText('마감하기'));
      fireEvent.click(screen.getByText('아니요'));

      expect(mockMutate).not.toHaveBeenCalled();
    });

    test('이벤트 마감 성공 시 데이터가 다시 로드된다', async () => {
      setupMockConfirm(true);
      renderEventManagePage();

      await waitFor(() => {
        expect(screen.getByText('마감하기')).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText('마감하기'));
      fireEvent.click(screen.getByText('네'));

      expect(mockMutate).toHaveBeenCalledWith(123, {
        onSuccess: expect.any(Function),
        onError: expect.any(Function),
      });
    });

    test('마감 성공 후 신청 마감일이 변경되어 표시되고 버튼이 "마감됨"으로 바뀐다', async () => {
      setupMockConfirm(true);

      renderEventManagePage();

      await waitFor(() => {
        expect(
          screen.getByText(
            (content) => content.startsWith('신청 마감:') && content.includes('2025.')
          )
        ).toBeInTheDocument();
        expect(screen.getByText('마감하기')).toBeInTheDocument();
      });

      fireEvent.click(screen.getByText('마감하기'));
      fireEvent.click(screen.getByText('네'));
      expect(mockMutate).toHaveBeenCalled();

      const updatedEventDetail = { ...mockEventDetail, registrationEnd: '2000-01-01T00:00:00' };
      mockEventDetailApiResponse(updatedEventDetail);

      const [, options] = mockMutate.mock.calls[0] as [number, { onSuccess: () => void }];
      options.onSuccess();

      await waitFor(() => {
        expect(
          screen.getByText(
            (content) => content.startsWith('신청 마감:') && content.includes('2000.')
          )
        ).toBeInTheDocument();
        expect(screen.getByText('마감됨')).toBeInTheDocument();
      });
    });
  });
});
