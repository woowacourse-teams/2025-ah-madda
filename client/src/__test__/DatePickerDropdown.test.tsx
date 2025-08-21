import { render, screen, fireEvent } from '@testing-library/react';
import { vi } from 'vitest';

import { DatePickerDropdown } from '@/features/Event/New/components/DatePickerDropdown';
import { useRangeDatePicker } from '@/features/Event/New/hooks/useRangeDatePicker';
import { useSingleDatePicker } from '@/features/Event/New/hooks/useSingleDatePicker';

import { ThemeProviderWrapper } from './customRender';

vi.mock('@/shared/hooks/useClickOutside', () => ({
  useClickOutside: vi.fn(),
}));

vi.mock('@/features/Event/New/hooks/useSingleDatePicker', () => ({
  useSingleDatePicker: vi.fn(),
}));

vi.mock('@/features/Event/New/hooks/useRangeDatePicker', () => ({
  useRangeDatePicker: vi.fn(),
}));

const mockUseSingleDatePicker = vi.mocked(useSingleDatePicker);
const mockUseRangeDatePicker = vi.mocked(useRangeDatePicker);

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
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={false}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
      );

      expect(container.firstChild).toBeNull();
    });
  });

  describe('single 모드', () => {
    beforeEach(() => {
      mockUseSingleDatePicker.mockReturnValue(mockSingleDatePickerReturn);
    });

    test('mode가 single이고 isOpen이 true일 때 단일 날짜 캘린더가 렌더링되어야 한다', () => {
      render(
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
            title="Test Title"
          />
        </ThemeProviderWrapper>
      );

      expect(screen.getByText('신청 마감 날짜 및 시간 선택')).toBeInTheDocument();
      expect(screen.getByText('취소')).toBeInTheDocument();
      expect(screen.getByText('확인')).toBeInTheDocument();
    });

    test('취소 버튼 클릭 시 handleCancel을 호출해야 한다', () => {
      render(
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
      );

      const cancelButton = screen.getByText('취소');
      fireEvent.click(cancelButton);

      expect(mockSingleDatePickerReturn.handleCancel).toHaveBeenCalled();
    });

    test('확인 버튼 클릭 시 handleConfirm을 호출해야 한다', () => {
      render(
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
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
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="single"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
      );

      const confirmButton = screen.getByText('확인');
      expect(confirmButton).toBeDisabled();
    });
  });

  describe('range 모드', () => {
    beforeEach(() => {
      mockUseRangeDatePicker.mockReturnValue(mockRangeDatePickerReturn);
    });

    test('mode가 range이고 isOpen이 true일 때 범위 날짜 캘린더가 렌더링되어야 한다', () => {
      render(
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
      );

      expect(screen.getByText('이벤트 날짜 및 시간 선택')).toBeInTheDocument();
      expect(screen.getByText('시작 시간')).toBeInTheDocument();
      expect(screen.getByText('종료 시간')).toBeInTheDocument();
      expect(screen.getByText('취소')).toBeInTheDocument();
      expect(screen.getByText('확인')).toBeInTheDocument();
    });

    test('취소 버튼 클릭 시 handleCancel을 호출해야 한다', () => {
      render(
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
      );

      const cancelButton = screen.getByText('취소');
      fireEvent.click(cancelButton);

      expect(mockRangeDatePickerReturn.handleCancel).toHaveBeenCalled();
    });

    test('확인 버튼 클릭 시 handleConfirm을 호출해야 한다', () => {
      render(
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
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
        <ThemeProviderWrapper>
          <DatePickerDropdown
            mode="range"
            isOpen={true}
            onClose={mockOnClose}
            onSelect={mockOnSelect}
          />
        </ThemeProviderWrapper>
      );

      const confirmButton = screen.getByText('확인');
      expect(confirmButton).toBeDisabled();
    });
  });
});
