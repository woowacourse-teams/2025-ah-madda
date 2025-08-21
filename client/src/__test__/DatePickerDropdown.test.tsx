import { ThemeProvider } from '@emotion/react';
import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';

import { DatePickerDropdown } from '@/features/Event/New/components/DatePickerDropdown';
import { useRangeDatePicker } from '@/features/Event/New/hooks/useRangeDatePicker';
import { useSingleDatePicker } from '@/features/Event/New/hooks/useSingleDatePicker';
import { useClickOutside } from '@/shared/hooks/useClickOutside';
import { theme } from '@/shared/styles/theme';

// Mock the hooks
vi.mock('@/shared/hooks/useClickOutside', () => ({
  useClickOutside: vi.fn(),
}));

vi.mock('@/features/Event/New/hooks/useSingleDatePicker', () => ({
  useSingleDatePicker: vi.fn(),
}));

vi.mock('@/features/Event/New/hooks/useRangeDatePicker', () => ({
  useRangeDatePicker: vi.fn(),
}));

const mockUseClickOutside = vi.mocked(useClickOutside);
const mockUseSingleDatePicker = vi.mocked(useSingleDatePicker);
const mockUseRangeDatePicker = vi.mocked(useRangeDatePicker);

const TestWrapper = ({ children }: { children: React.ReactNode }) => (
  <ThemeProvider theme={theme}>{children}</ThemeProvider>
);

const mockSingleDatePickerReturn = {
  selectedDate: new Date('2024-01-15'),
  selectedTime: { hours: 14, minutes: 30 },
  setSelectedTime: vi.fn(),
  handleDateSelect: vi.fn(),
  handleConfirm: vi.fn(),
  handleCancel: vi.fn(),
  isConfirmDisabled: false,
};

const mockRangeDatePickerReturn = {
  selectedStartDate: new Date('2024-01-15'),
  selectedEndDate: new Date('2024-01-16'),
  selectedStartTime: { hours: 14, minutes: 30 },
  selectedEndTime: { hours: 16, minutes: 30 },
  setSelectedStartTime: vi.fn(),
  setSelectedEndTime: vi.fn(),
  handleDateSelect: vi.fn(),
  handleDateRangeSelect: vi.fn(),
  handleConfirm: vi.fn(),
  handleCancel: vi.fn(),
  isConfirmDisabled: false,
};

describe('DatePickerDropdown', () => {
  const mockOnClose = vi.fn();
  const mockOnSelect = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('isOpen이 false일 때', () => {
    test('아무것도 렌더링하지 않아야 한다', () => {
      const { container } = render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={false}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      expect(container.firstChild).toBeNull();
    });
  });

  describe('single 모드', () => {
    beforeEach(() => {
      mockUseSingleDatePicker.mockReturnValue(mockSingleDatePickerReturn);
    });

    test('mode가 single이고 isOpen이 true일 때 단일 날짜 선택기가 렌더링되어야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
            title="Test Title"
          />
        </TestWrapper>
      );

      expect(screen.getByText('신청 마감 날짜 및 시간 선택')).toBeInTheDocument();
      expect(screen.getByText('취소')).toBeInTheDocument();
      expect(screen.getByText('확인')).toBeInTheDocument();
    });

    test('올바른 props로 useSingleDatePicker를 호출해야 한다', () => {
      const initialDate = new Date('2024-01-10');
      const initialTime = { hours: 10, minutes: 0 };

      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
            initialDate={initialDate}
            initialTime={initialTime}
          />
        </TestWrapper>
      );

      expect(mockUseSingleDatePicker).toHaveBeenCalledWith({
        onClose: mockOnClose,
        onSelect: mockOnSelect,
        initialDate,
        initialTime,
      });
    });

    test('취소 버튼 클릭 시 handleCancel을 호출해야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      const cancelButton = screen.getByText('취소');
      fireEvent.click(cancelButton);

      expect(mockSingleDatePickerReturn.handleCancel).toHaveBeenCalled();
    });

    test('확인 버튼 클릭 시 handleConfirm을 호출해야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      const confirmButton = screen.getByText('확인');
      fireEvent.click(confirmButton);

      expect(mockSingleDatePickerReturn.handleConfirm).toHaveBeenCalled();
    });

    test('isConfirmDisabled가 true일 때 확인 버튼이 비활성화되어야 한다', () => {
      mockUseSingleDatePicker.mockReturnValue({
        ...mockSingleDatePickerReturn,
        isConfirmDisabled: true,
      });

      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      const confirmButton = screen.getByText('확인');
      expect(confirmButton).toBeDisabled();
    });
  });

  describe('range 모드', () => {
    beforeEach(() => {
      mockUseRangeDatePicker.mockReturnValue(mockRangeDatePickerReturn);
    });

    test('mode가 range이고 isOpen이 true일 때 범위 날짜 선택기가 렌더링되어야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      expect(screen.getByText('이벤트 날짜 및 시간 선택')).toBeInTheDocument();
      expect(screen.getByText('시작 시간')).toBeInTheDocument();
      expect(screen.getByText('종료 시간')).toBeInTheDocument();
      expect(screen.getByText('취소')).toBeInTheDocument();
      expect(screen.getByText('확인')).toBeInTheDocument();
    });

    test('올바른 props로 useRangeDatePicker를 호출해야 한다', () => {
      const initialStartDate = new Date('2024-01-10');
      const initialEndDate = new Date('2024-01-12');
      const initialStartTime = { hours: 10, minutes: 0 };
      const initialEndTime = { hours: 18, minutes: 0 };

      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
            initialStartDate={initialStartDate}
            initialEndDate={initialEndDate}
            initialStartTime={initialStartTime}
            initialEndTime={initialEndTime}
          />
        </TestWrapper>
      );

      expect(mockUseRangeDatePicker).toHaveBeenCalledWith({
        onClose: mockOnClose,
        onSelect: mockOnSelect,
        initialStartDate,
        initialEndDate,
        initialStartTime,
        initialEndTime,
      });
    });

    test('취소 버튼 클릭 시 handleCancel을 호출해야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      const cancelButton = screen.getByText('취소');
      fireEvent.click(cancelButton);

      expect(mockRangeDatePickerReturn.handleCancel).toHaveBeenCalled();
    });

    test('확인 버튼 클릭 시 handleConfirm을 호출해야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      const confirmButton = screen.getByText('확인');
      fireEvent.click(confirmButton);

      expect(mockRangeDatePickerReturn.handleConfirm).toHaveBeenCalled();
    });

    test('isConfirmDisabled가 true일 때 확인 버튼이 비활성화되어야 한다', () => {
      mockUseRangeDatePicker.mockReturnValue({
        ...mockRangeDatePickerReturn,
        isConfirmDisabled: true,
      });

      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      const confirmButton = screen.getByText('확인');
      expect(confirmButton).toBeDisabled();
    });
  });

  describe('useClickOutside 통합', () => {
    test('올바른 매개변수로 useClickOutside를 호출해야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      expect(mockUseClickOutside).toHaveBeenCalledWith({
        ref: expect.any(Object),
        isOpen: true,
        onClose: mockOnClose,
      });
    });

    test('isOpen이 false일 때도 useClickOutside를 호출해야 한다', () => {
      render(
        <TestWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={false}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </TestWrapper>
      );

      expect(mockUseClickOutside).toHaveBeenCalledWith({
        ref: expect.any(Object),
        isOpen: false,
        onClose: mockOnClose,
      });
    });
  });
});
