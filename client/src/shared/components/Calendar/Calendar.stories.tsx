import { useState } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { Flex } from '../Flex';
import { Text } from '../Text';

import { Calendar } from './Calendar';

const meta = {
  title: 'components/Calendar',
  component: Calendar,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'Calendar component allows users to select dates with single or range selection modes.',
      },
    },
  },
  argTypes: {
    mode: {
      control: { type: 'radio' },
      options: ['single', 'range'],
      description: 'Selection mode for the calendar',
    },
    selectedDate: {
      control: false,
      description: 'Currently selected start date',
    },
    selectedEndDate: {
      control: false,
      description: 'Currently selected end date (for range mode)',
    },
    onSelectDate: {
      control: false,
      description: 'Callback when a single date is selected',
    },
    onSelectDateRange: {
      control: false,
      description: 'Callback when a date range is completed',
    },
  },
} satisfies Meta<typeof Calendar>;

export default meta;
type Story = StoryObj<typeof Calendar>;

const CalendarComponent = ({ mode }: { mode: 'single' | 'range' }) => {
  const [selectedDate, setSelectedDate] = useState<Date | null>(null);
  const [selectedEndDate, setSelectedEndDate] = useState<Date | null>(null);

  const handleDateSelect = (date: Date) => {
    setSelectedDate(date);
    setSelectedEndDate(null);
  };

  const handleDateRangeSelect = (startDate: Date, endDate: Date) => {
    setSelectedDate(startDate);
    setSelectedEndDate(endDate);
  };

  return (
    <Flex dir="column" gap="16px">
      <Calendar
        mode={mode}
        selectedDate={selectedDate}
        selectedEndDate={selectedEndDate}
        onSelectDate={handleDateSelect}
        onSelectDateRange={handleDateRangeSelect}
      />

      {mode === 'single' ? (
        <Flex dir="column" gap="8px">
          <Text type="Body" weight="medium">
            Selected Date:
          </Text>
          <Text type="Body" color="primary">
            {selectedDate ? selectedDate.toLocaleDateString('ko-KR') : 'None'}
          </Text>
        </Flex>
      ) : (
        <Flex dir="column" gap="8px">
          <Text type="Body" weight="medium">
            Selected Date Range:
          </Text>
          <Text type="Body" color="primary">
            Start: {selectedDate ? selectedDate.toLocaleDateString('ko-KR') : 'None'}
          </Text>
          <Text type="Body" color="primary">
            End:{' '}
            {selectedEndDate
              ? selectedEndDate.toLocaleDateString('ko-KR')
              : selectedDate
                ? selectedDate.toLocaleDateString('ko-KR')
                : 'None'}
          </Text>
          {selectedDate && !selectedEndDate && (
            <Text type="Label" color="gray">
              Click the same date again to set as end date, or select a different date for range
            </Text>
          )}
        </Flex>
      )}
    </Flex>
  );
};

export const Default: Story = {
  args: {
    mode: 'single',
  },
  render: (args) => <CalendarComponent mode={args.mode || 'single'} />,
  parameters: {
    docs: {
      description: {
        story:
          'Interactive calendar component. Use the controls to switch between single and range selection modes.',
      },
    },
  },
};

export const SingleMode: Story = {
  render: () => <CalendarComponent mode="single" />,
  parameters: {
    docs: {
      description: {
        story: 'Single date selection mode. Click a date to select it.',
      },
    },
  },
};

export const RangeMode: Story = {
  render: () => <CalendarComponent mode="range" />,
  parameters: {
    docs: {
      description: {
        story:
          'Range date selection mode. Click a date for start, then click another date for end. Click the same date twice to create a single-day range.',
      },
    },
  },
};
