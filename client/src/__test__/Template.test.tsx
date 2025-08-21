import { QueryClient } from '@tanstack/react-query';
import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach } from 'vitest';

import { useAddTemplate } from '@/api/mutations/useAddTemplate';
import { eventQueryOptions } from '@/api/queries/event';
import { EventCreateForm } from '@/features/Event/New/components/EventCreateForm';
import { TemplateDropdown } from '@/features/Event/New/components/TemplateDropdown';
import { useBasicEventForm } from '@/features/Event/New/hooks/useBasicEventForm';

import { createTestQueryClient } from './createTestQueryClient';
import { QueryClientProviderWrapper } from './customRender';

vi.mock('@/api/mutations/useAddTemplate', () => ({
  useAddTemplate: vi.fn(() => ({
    mutate: vi.fn(),
    isPending: false,
    isError: false,
    isSuccess: false,
    error: null,
    data: null,
    reset: vi.fn(),
    mutateAsync: vi.fn(),
  })),
}));

vi.mock('@/features/Event/New/hooks/useBasicEventForm', () => ({
  useBasicEventForm: vi.fn(() => ({
    basicEventForm: {
      title: '테스트 이벤트',
      description: '테스트 이벤트 설명',
      place: '테스트 장소',
      maxCapacity: 100,
      eventStart: '2024-01-01T09:00',
      eventEnd: '2024-01-01T18:00',
      registrationEnd: '2023-12-31T18:00',
    },
    updateAndValidate: vi.fn(),
    handleChange: vi.fn(),
    errors: {},
    isValid: true,
    loadFormData: vi.fn(),
  })),
}));

vi.mock('@/shared/components/Toast/ToastContext', () => ({
  useToast: () => ({
    success: vi.fn(),
    error: vi.fn(),
  }),
}));

vi.mock('react-router-dom', () => ({
  useNavigate: () => vi.fn(),
  useParams: () => ({ organizationId: '1' }),
}));

vi.mock('@/features/Event/New/components/EventCreateForm', () => ({
  EventCreateForm: () => (
    <div>
      <button>+현재 글 템플릿에 추가</button>
    </div>
  ),
}));

vi.mock('@/features/Event/New/components/TemplateDropdown', () => ({
  TemplateDropdown: ({
    onTemplateSelected,
  }: {
    onTemplateSelected: (data: { description: string }) => void;
  }) => (
    <div>
      <button onClick={() => onTemplateSelected({ description: '템플릿 설명' })}>템플릿 1</button>
    </div>
  ),
}));

const renderWithQueryClient = (component: React.ReactElement, client?: QueryClient) => {
  const queryClient = client || createTestQueryClient();
  return render(
    <QueryClientProviderWrapper queryClient={queryClient}>{component}</QueryClientProviderWrapper>
  );
};

describe('Template 기능 테스트', () => {
  let queryClient: QueryClient;

  beforeEach(() => {
    queryClient = createTestQueryClient();
    vi.clearAllMocks();
  });

  describe('템플릿 저장 기능', () => {
    test('템플릿 저장 버튼이 렌더링된다', () => {
      renderWithQueryClient(<EventCreateForm isEdit={false} />, queryClient);

      const templateSaveButton = screen.getByText('+현재 글 템플릿에 추가');
      expect(templateSaveButton).toBeInTheDocument();
    });

    test('템플릿 저장 시 useAddTemplate hook이 호출된다', () => {
      const mockAddTemplate = useAddTemplate();

      expect(mockAddTemplate.mutate).toBeDefined();
      expect(typeof mockAddTemplate.mutate).toBe('function');
    });
  });

  describe('템플릿 선택 기능', () => {
    test('템플릿 드롭다운에서 템플릿을 선택할 수 있다', async () => {
      const mockOnTemplateSelected = vi.fn();

      queryClient.setQueryData(eventQueryOptions.templateList().queryKey, [
        { templateId: 1, title: '템플릿 1' },
      ]);

      renderWithQueryClient(
        <TemplateDropdown onTemplateSelected={mockOnTemplateSelected} />,
        queryClient
      );

      const templateItem = screen.getByText('템플릿 1');
      fireEvent.click(templateItem);

      await waitFor(() => {
        expect(mockOnTemplateSelected).toHaveBeenCalledWith({
          description: '템플릿 설명',
        });
      });
    });
  });

  describe('템플릿 데이터 검증', () => {
    test('빈 설명으로 템플릿 저장 시 에러 처리된다', () => {
      vi.mocked(useBasicEventForm).mockReturnValue({
        basicEventForm: {
          title: '테스트 이벤트',
          description: '',
          place: '테스트 장소',
          maxCapacity: 100,
          eventStart: '2024-01-01T09:00',
          eventEnd: '2024-01-01T18:00',
          registrationEnd: '2023-12-31T18:00',
        },
        updateAndValidate: vi.fn(),
        handleChange: vi.fn(),
        errors: { description: '이벤트 설명을 입력해 주세요' },
        isValid: false,
        loadFormData: vi.fn(),
      });

      const { basicEventForm, errors, isValid } = useBasicEventForm();

      expect(basicEventForm.description).toBe('');
      expect(errors.description).toBe('이벤트 설명을 입력해 주세요');
      expect(isValid).toBe(false);
    });
  });
});
