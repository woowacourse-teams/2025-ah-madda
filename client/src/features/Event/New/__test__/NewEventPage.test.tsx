import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { RouterWithQueryClient } from '@/__test__/customRender';
import { fetcher } from '@/api/fetcher';

import { NewEventPage } from '../pages/NewEventPage';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    post: vi.fn(),
    get: vi.fn(),
  },
}));

vi.mock('@/shared/lib/gaEvents', () => ({
  trackCreateEvent: vi.fn(),
}));

vi.mock('../hooks/useAddEvent', () => ({
  useAddEvent: () => ({
    mutate: vi.fn(),
  }),
}));

vi.mock('@/api/mutations/useUpdateEvent', () => ({
  useUpdateEvent: () => ({
    mutate: vi.fn(),
  }),
}));

vi.mock('@/api/mutations/useAddTemplate', () => ({
  useAddTemplate: () => ({
    mutate: vi.fn(),
  }),
}));

vi.mock('../hooks/useBasicEventForm', () => ({
  useBasicEventForm: () => ({
    basicEventForm: {
      title: '',
      description: '',
      place: '',
      eventStart: '',
      eventEnd: '',
      registrationEnd: '',
      maxCapacity: 0,
    },
    handleValueChange: vi.fn(),
    validateField: vi.fn(),
    handleChange: vi.fn(),
    errors: {},
    isValid: false,
    loadFormData: vi.fn(),
  }),
}));

vi.mock('../hooks/useQuestionForm', () => ({
  useQuestionForm: () => ({
    questions: [],
    addQuestion: vi.fn(),
    deleteQuestion: vi.fn(),
    updateQuestion: vi.fn(),
    isValid: true,
  }),
}));

vi.mock('../components/TemplateModal', () => ({
  TemplateModal: ({ isOpen }: { isOpen: boolean }) =>
    isOpen ? <div data-testid="template-modal">템플릿 불러오기</div> : null,
}));

vi.mock('../components/MaxCapacityModal', () => ({
  MaxCapacityModal: ({ isOpen }: { isOpen: boolean }) =>
    isOpen ? <div data-testid="capacity-modal">최대 수용 인원</div> : null,
}));

vi.mock('../components/QuestionForm', () => ({
  QuestionForm: () => (
    <div>
      <div>사전 질문</div>
      <button>질문 추가</button>
      <div>참가자에게 묻고 싶은 질문을 추가해 보세요.</div>
    </div>
  ),
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

describe('NewEventPage', () => {
  beforeEach(() => {
    vi.clearAllMocks();

    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('templates')) {
        return Promise.resolve([]);
      }
      if (url.includes('events')) {
        return Promise.resolve([]);
      }
      if (url.includes('titles')) {
        return Promise.resolve([]);
      }
      return Promise.resolve([]);
    });

    mockFetcher.post.mockResolvedValue({});
  });

  describe('기본 렌더링 테스트', () => {
    test('NewEventPage 컴포넌트가 에러 없이 렌더링된다', () => {
      expect(() => {
        render(
          <RouterWithQueryClient
            initialRoute="/event/new"
            routes={[{ path: '/event/new', element: <NewEventPage /> }]}
          />
        );
      }).not.toThrow();
    });

    test('입력 필드들이 렌더링된다', async () => {
      render(
        <RouterWithQueryClient
          initialRoute="/event/new"
          routes={[{ path: '/event/new', element: <NewEventPage /> }]}
        />
      );

      await waitFor(() => {
        expect(screen.getByLabelText(/이벤트 이름/)).toBeInTheDocument();
      });

      expect(screen.getByLabelText(/이벤트 시작일/)).toBeInTheDocument();
      expect(screen.getByLabelText(/이벤트 종료일/)).toBeInTheDocument();
      expect(screen.getByLabelText(/신청 종료일/)).toBeInTheDocument();
      expect(screen.getByLabelText(/이벤트 장소/)).toBeInTheDocument();
      expect(screen.getByLabelText(/소개글/)).toBeInTheDocument();
      expect(screen.getByText(/사전 질문/)).toBeInTheDocument();
      expect(screen.getByText(/질문 추가/)).toBeInTheDocument();
      expect(screen.getByRole('button', { name: /이벤트 생성하기/ })).toBeInTheDocument();
    });

    test('폼이 초기화된 상태로 렌더링된다', () => {
      render(
        <RouterWithQueryClient
          initialRoute="/event/new"
          routes={[{ path: '/event/new', element: <NewEventPage /> }]}
        />
      );

      expect(screen.getByLabelText(/이벤트 이름/)).toHaveValue('');
      expect(screen.getByLabelText(/이벤트 장소/)).toHaveValue('');
      expect(screen.getByLabelText(/소개글/)).toHaveValue('');
    });
  });

  describe('폼 기능 테스트', () => {
    test('폼이 유효하지 않으면 제출 버튼이 비활성화된다', () => {
      render(
        <RouterWithQueryClient
          initialRoute="/event/new"
          routes={[{ path: '/event/new', element: <NewEventPage /> }]}
        />
      );

      const submitButton = screen.getByRole('button', { name: /이벤트 생성하기/ });
      expect(submitButton).toBeDisabled();
    });
  });
});
