import type { Meta, StoryObj } from '@storybook/react';

import { RequiredMark } from './RequiredMark';

const meta = {
  title: 'components/RequiredMark',
  component: RequiredMark,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A small decorative asterisk used next to form labels to indicate a required field.',
      },
    },
  },
} satisfies Meta<typeof RequiredMark>;

export default meta;
type Story = StoryObj<typeof RequiredMark>;

export const Basic: Story = {};
