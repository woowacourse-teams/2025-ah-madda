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

export const Basic: Story = {
  args: {
    mode: 'single',
  },
  render: (args) => {
    const BasicComponent = () => {
      const [selectedDate, setSelectedDate] = useState<Date | null>(null);
      const [selectedEndDate, setSelectedEndDate] = useState<Date | null>(null);

      const handleDateSelect = (date: Date) => {
        setSelectedDate(date);
        if (args.mode === 'single') {
          setSelectedEndDate(null);
        }
      };

      const handleDateRangeSelect = (startDate: Date, endDate: Date) => {
        setSelectedDate(startDate);
        setSelectedEndDate(endDate);
      };

      return (
        <Flex dir="column" gap="16px">
          <Calendar
            {...args}
            selectedDate={selectedDate}
            selectedEndDate={selectedEndDate}
            onSelectDate={handleDateSelect}
            onSelectDateRange={handleDateRangeSelect}
          />

          {args.mode === 'single' ? (
            <Flex dir="column" gap="8px">
              <Text type="Body" weight="medium">
                선택된 날짜:
              </Text>
              <Text type="Body" color="primary">
                {selectedDate ? selectedDate.toLocaleDateString('ko-KR') : '없음'}
              </Text>
            </Flex>
          ) : (
            <Flex dir="column" gap="8px">
              <Text type="Body" weight="medium">
                선택된 날짜 범위:
              </Text>
              <Text type="Body" color="primary">
                시작일: {selectedDate ? selectedDate.toLocaleDateString('ko-KR') : '없음'}
              </Text>
              <Text type="Body" color="primary">
                종료일:{' '}
                {selectedEndDate
                  ? selectedEndDate.toLocaleDateString('ko-KR')
                  : selectedDate
                    ? selectedDate.toLocaleDateString('ko-KR')
                    : '없음'}
              </Text>
              {selectedDate && !selectedEndDate && (
                <Text type="Label" color="gray">
                  종료일을 선택해주세요
                </Text>
              )}
            </Flex>
          )}
        </Flex>
      );
    };

    return <BasicComponent />;
  },
};
