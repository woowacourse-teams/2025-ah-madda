import type { Meta, StoryObj } from '@storybook/react';

import { Text } from './Text';

const meta = {
  title: 'components/Text',
  component: Text,
  tags: ['autodocs'],
} satisfies Meta<typeof Text>;

export default meta;
type Story = StoryObj<typeof Text>;

export const Basic: Story = {
  args: {
    as: 'p',
    type: 'Body',
    weight: 'regular',
    color: 'black',
    children: 'This is a basic text component.',
  },
  argTypes: {
    as: {
      control: false,
    },
  },
  render: (args) => <Text {...args}>{args.children}</Text>,
};
