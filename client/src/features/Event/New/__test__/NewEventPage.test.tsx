import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { describe, expect, vi, beforeEach } from 'vitest';

import { NewEventPage } from '../pages/NewEventPage';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    post: vi.fn(),
  },
}));

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

    expect(screen.getByLabelText(/이벤트 이름/)).toBeInTheDocument();
    expect(screen.getByLabelText(/이벤트 시작일/)).toBeInTheDocument();
    expect(screen.getByLabelText(/이벤트 종료일/)).toBeInTheDocument();
    expect(screen.getByLabelText(/신청 종료일/)).toBeInTheDocument();
    expect(screen.getByLabelText(/장소/)).toBeInTheDocument();
    expect(screen.getByLabelText(/설명/)).toBeInTheDocument();
    expect(screen.getByText(/사전 질문/)).toBeInTheDocument();
    expect(screen.getByText(/질문 추가/)).toBeInTheDocument();
    expect(screen.getByRole('button', { name: /이벤트 만들기/ })).toBeInTheDocument();
  });

  test('폼이 초기화된 상태로 렌더링된다', () => {
    render(<TestWrapper />);

    expect(screen.getByLabelText(/이벤트 이름/)).toHaveValue('');
    expect(screen.getByLabelText(/장소/)).toHaveValue('');
    expect(screen.getByLabelText(/설명/)).toHaveValue('');
    expect(screen.getByLabelText(/수용 인원/)).toHaveValue('무제한');
  });

  test('템플릿 버튼 클릭 시 템플릿 모달이 열린다', async () => {
    const user = userEvent.setup();
    render(<TestWrapper />);

    await user.click(screen.getByRole('button', { name: /템플릿/ }));

    expect(await screen.findByText(/템플릿 불러오기/)).toBeInTheDocument();
  });

  test('수용 인원 필드 클릭 시 수용 인원 모달이 열린다', async () => {
    const user = userEvent.setup();
    render(<TestWrapper />);

    await user.click(screen.getByLabelText(/수용 인원/));

    expect(await screen.findByText(/수용 인원을 입력해주세요/)).toBeInTheDocument();
  });

  test('사전 질문 추가 시 항목이 추가된다', async () => {
    const user = userEvent.setup();
    render(<TestWrapper />);

    const addButton = screen.getByRole('button', { name: /질문 추가/ });

    await user.click(addButton);

    expect(screen.getByPlaceholderText(/질문을 입력해주세요/)).toBeInTheDocument();
  });

  test('폼이 유효하지 않으면 제출 버튼이 비활성화된다', () => {
    render(<TestWrapper />);

    const submitButton = screen.getByRole('button', { name: /이벤트 만들기/ });
    expect(submitButton).toBeDisabled();
  });
});
