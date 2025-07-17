import type { Meta, StoryObj } from '@storybook/react';

import { ProgressBar } from './ProgressBar';

const meta = {
  title: 'components/ProgressBar',
  component: ProgressBar,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A customizable progress bar component with optional animation, colors, and rounded corners.',
      },
    },
  },
} satisfies Meta<typeof ProgressBar>;

export default meta;
type Story = StoryObj<typeof ProgressBar>;

export const Basic: Story = {
  args: {
    value: 50,
    max: 100,
  },
};

export const CustomColor: Story = {
  args: {
    value: 75,
    max: 100,
    color: '#d0698b',
    backgroundColor: '#e2e2e2',
  },
};

export const AnimatedOff: Story = {
  args: {
    value: 80,
    max: 100,
    animated: false,
    color: '#2563EB',
  },
};
