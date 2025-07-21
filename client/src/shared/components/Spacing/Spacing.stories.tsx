import type { Meta, StoryObj } from '@storybook/react';

import { Spacing } from './Spacing';

const meta = {
  title: 'components/Spacing',
  component: Spacing,
  tags: ['autodocs'],
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
