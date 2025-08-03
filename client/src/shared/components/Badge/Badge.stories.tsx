import type { Meta, StoryObj } from '@storybook/react';

import { Badge } from './Badge';

const meta: Meta<typeof Badge> = {
  title: 'Components/Badge',
  component: Badge,
  parameters: {
    layout: 'centered',
    docs: {
      description: {
        component:
          'A badge component for displaying status information with different visual styles based on the type. Supports three types: recruiting, scheduled, and closed.',
      },
    },
  },
  tags: ['autodocs'],
  argTypes: {
    type: {
      control: { type: 'select' },
      options: ['모집중', '예정', '마감'],
      description: 'The type of badge to display',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    type: '모집중',
  },
};

export const AllTypes: Story = {
  render: () => (
    <div style={{ display: 'flex', gap: '16px', alignItems: 'center' }}>
      <Badge type="모집중" />
      <Badge type="예정" />
      <Badge type="마감" />
    </div>
  ),
};
