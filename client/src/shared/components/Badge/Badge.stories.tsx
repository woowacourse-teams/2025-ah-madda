import type { Meta, StoryObj } from '@storybook/react';

import { Flex } from '../Flex';

import { Badge } from './Badge';

const meta: Meta<typeof Badge> = {
  title: 'Components/Badge',
  component: Badge,
  parameters: {
    layout: 'centered',
    docs: {
      description: {
        component:
          'A badge component for displaying status information with different visual styles based on the variant. Supports three variants: blue, gray, and red.',
      },
    },
  },
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: { type: 'select' },
      options: ['blue', 'gray', 'red'],
      description: 'The variant of badge to display',
    },
  },
};

export default meta;
type Story = StoryObj<typeof meta>;

export const Default: Story = {
  args: {
    variant: 'blue',
    children: '모집중',
  },
};

export const AllVariants: Story = {
  render: () => (
    <Flex gap="16px" alignItems="center">
      <Badge variant="blue">모집중</Badge>
      <Badge variant="gray">예정</Badge>
      <Badge variant="red">신청마감</Badge>
    </Flex>
  ),
};
