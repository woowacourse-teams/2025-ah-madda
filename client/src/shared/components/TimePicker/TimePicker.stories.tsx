import { useState } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { formatTimeDisplay } from '@/shared/utils/timePicker';

import { Flex } from '../Flex';
import { Text } from '../Text';

import { TimePicker } from './TimePicker';

const meta = {
  title: 'components/TimePicker',
  component: TimePicker,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'TimePicker component allows users to select time in 10-minute intervals using dropdowns.',
      },
    },
  },
  argTypes: {
    selectedTime: {
      control: false,
      description: 'Currently selected time',
    },
    onTimeChange: {
      control: false,
      description: 'Callback when time is changed',
    },
    label: {
      control: { type: 'text' },
      description: 'Label for the time picker',
    },
    disabled: {
      control: { type: 'boolean' },
      description: 'Whether the time picker is disabled',
    },
  },
} satisfies Meta<typeof TimePicker>;

export default meta;
type Story = StoryObj<typeof TimePicker>;

export const Basic: Story = {
  args: {
    label: '시간 선택',
    disabled: false,
  },
  render: (args) => {
    const BasicComponent = () => {
      const [selectedTime, setSelectedTime] = useState<Date | undefined>();

      return (
        <Flex dir="column" gap="16px">
          <TimePicker {...args} selectedTime={selectedTime} onTimeChange={setSelectedTime} />
          <Text type="Body" color="primary">
            선택된 시간:
            {selectedTime
              ? formatTimeDisplay(selectedTime.getHours(), selectedTime.getMinutes())
              : '없음'}
          </Text>
        </Flex>
      );
    };

    return <BasicComponent />;
  },
};
