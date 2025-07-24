import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '../../../../api/fetcher';
import { NewEventPage } from '../pages/NewEventPage';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const TestWrapper = () => {
  const queryClient = new QueryClient({
    defaultOptions: {
      queries: { retry: false },
      mutations: { retry: false },
    },
  });

  return (
    <QueryClientProvider client={queryClient}>
      <MemoryRouter initialEntries={['/event/new']}>
        <Routes>
          <Route path="/event/new" element={<NewEventPage />} />
        </Routes>
      </MemoryRouter>
    </QueryClientProvider>
  );
};

describe('NewEventPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('입력 필드들이 렌더링된다', async () => {
    render(<TestWrapper />);

    expect(screen.getByLabelText('이벤트 이름')).toBeInTheDocument();
    expect(screen.getByLabelText('이벤트 시작 날짜/시간')).toBeInTheDocument();
    expect(screen.getByLabelText('이벤트 종료 날짜/시간')).toBeInTheDocument();
    expect(screen.getByLabelText('신청 시작 날짜/시간')).toBeInTheDocument();
    expect(screen.getByLabelText('신청 종료 날짜/시간')).toBeInTheDocument();
    expect(screen.getByLabelText('장소')).toBeInTheDocument();
    expect(screen.getByLabelText('설명')).toBeInTheDocument();
    expect(screen.getByLabelText('주최자 이름')).toBeInTheDocument();
    expect(screen.getByLabelText('수용 인원')).toBeInTheDocument();
    expect(screen.getByText('사전 질문')).toBeInTheDocument();
    expect(screen.getByText(/질문 추가/)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /이벤트 만들기/ })).toBeInTheDocument();
  });

  test('필수 필드를 모두 채우고 등록 버튼을 누르면 API가 호출된다', async () => {
    const user = userEvent.setup();
    render(<TestWrapper />);

    await user.type(screen.getByLabelText('이벤트 이름'), '테스트 제목');
    await user.type(screen.getByLabelText('이벤트 시작 날짜/시간'), '2025.07.30 13:00');
    await user.type(screen.getByLabelText('이벤트 종료 날짜/시간'), '2025.07.30 15:00');
    await user.type(screen.getByLabelText('신청 시작 날짜/시간'), '2025.07.25 13:00');
    await user.type(screen.getByLabelText('신청 종료 날짜/시간'), '2025.07.25 15:00');
    await user.type(screen.getByLabelText('장소'), '서울시 강남구');
    await user.type(screen.getByLabelText('설명'), '이벤트에 대한 설명입니다.');
    await user.type(screen.getByLabelText('주최자 이름'), '홍길동');
    await user.type(screen.getByLabelText('수용 인원'), '10');

    await user.click(screen.getByRole('button', { name: /이벤트 만들기/ }));

    await waitFor(() => {
      expect(mockFetcher.post).toHaveBeenCalled();
    });
  });
});
