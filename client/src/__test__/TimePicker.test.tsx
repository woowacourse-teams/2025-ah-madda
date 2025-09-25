import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import { TimePicker } from '@/shared/components/TimePicker';

import { ThemeProviderWrapper } from './customRender';

describe('TimePicker', () => {
  const mockOnTimeChange = vi.fn();

  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('같은 날짜에서 minTime보다 이후 시간들이 비활성화된다', async () => {
    const eventStartTime = new Date('2025-12-15T14:30:00');
    const selectedDate = new Date('2025-12-15');

    render(
      <ThemeProviderWrapper>
        <TimePicker
          selectedTime={undefined}
          onTimeChange={mockOnTimeChange}
          minTime={eventStartTime}
          selectedDate={selectedDate}
        />
      </ThemeProviderWrapper>
    );

    const hourSelect = screen.getByLabelText('시 선택');
    const hourOptions = Array.from(hourSelect.querySelectorAll('option')) as HTMLOptionElement[];

    for (let hour = 0; hour < 14; hour++) {
      const hourOption = hourOptions.find((opt) => opt.value === hour.toString());
      if (hourOption) {
        expect(hourOption.disabled).toBe(false);
      }
    }

    const hour14 = hourOptions.find((opt) => opt.value === '14');
    expect(hour14?.disabled).toBe(false);

    const hour15 = hourOptions.find((opt) => opt.value === '15');
    expect(hour15?.disabled).toBe(true);
  });

  test('같은 날짜에서 같은 시간일 때 minTime보다 이후 minute이 비활성화된다', async () => {
    const user = userEvent.setup();
    const eventStartTime = new Date('2025-12-15T14:30:00');
    const selectedDate = new Date('2025-12-15');
    const selectedTime = new Date('2025-12-15T14:00:00');

    render(
      <ThemeProviderWrapper>
        <TimePicker
          selectedTime={selectedTime}
          onTimeChange={mockOnTimeChange}
          minTime={eventStartTime}
          selectedDate={selectedDate}
        />
      </ThemeProviderWrapper>
    );

    const hourSelect = screen.getByLabelText('시 선택');
    const minuteSelect = screen.getByLabelText('분 선택');

    await user.selectOptions(hourSelect, '14');

    const minuteOptions = Array.from(
      minuteSelect.querySelectorAll('option')
    ) as HTMLOptionElement[];

    const enabledMinutes = [0, 10, 20];
    enabledMinutes.forEach((minute) => {
      const minuteOption = minuteOptions.find((opt) => opt.value === minute.toString());
      if (minuteOption) {
        expect(minuteOption.disabled).toBe(false);
      }
    });

    const minute30Option = minuteOptions.find((opt) => opt.value === '30');
    expect(minute30Option?.disabled).toBe(false);

    const disabledMinutes = [40, 50];
    disabledMinutes.forEach((minute) => {
      const minuteOption = minuteOptions.find((opt) => opt.value === minute.toString());
      if (minuteOption) {
        expect(minuteOption.disabled).toBe(true);
      }
    });
  });

  test('다른 날짜에서는 시간 제한이 적용되지 않는다', () => {
    const eventStartTime = new Date('2025-12-15T14:30:00');
    const selectedDate = new Date('2025-12-16');

    render(
      <ThemeProviderWrapper>
        <TimePicker
          selectedTime={undefined}
          onTimeChange={mockOnTimeChange}
          minTime={eventStartTime}
          selectedDate={selectedDate}
        />
      </ThemeProviderWrapper>
    );

    const hourSelect = screen.getByLabelText('시 선택');
    const hourOptions = Array.from(hourSelect.querySelectorAll('option')) as HTMLOptionElement[];

    const enabledHours = hourOptions.filter((opt) => opt.value !== '' && !opt.disabled);

    expect(enabledHours.length).toBe(24);
  });
});
