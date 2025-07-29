import type { Meta, StoryObj } from '@storybook/react';

import { Spacing } from './Spacing';

const meta = {
  title: 'components/Spacing',
  component: Spacing,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A simple spacing component that provides a flexible way to add vertical or horizontal space between elements.',
      },
    },
  },
} satisfies Meta<typeof Spacing>;

export default meta;
type Story = StoryObj<typeof Spacing>;

export const Basic: Story = {
  args: {
    height: '1px',
    color: 'gray',
  },
  render: (args) => <Spacing {...args} />,
};
