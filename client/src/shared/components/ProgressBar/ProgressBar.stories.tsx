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

export const CustomHeightAndColor: Story = {
  args: {
    value: 75,
    height: '12px',
    color: '#d0698b',
    backgroundColor: '#e2e2e2',
  },
};

export const Squared: Story = {
  args: {
    value: 30,
    borderRadius: '0px',
    height: '10px',
    color: '#FF6B6B',
    backgroundColor: '#FFECEC',
  },
};

export const AnimatedOff: Story = {
  args: {
    value: 80,
    animated: false,
    color: '#2563EB',
  },
};
