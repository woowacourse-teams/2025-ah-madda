import type { Meta, StoryObj } from '@storybook/react';

import { iconNames } from '../Icon/assets';

import { IconButton } from './IconButton';

const meta = {
  title: 'components/IconButton',
  component: IconButton,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'The IconButton component is a button that displays an icon, providing a compact and interactive way to trigger actions.',
      },
    },
  },
} satisfies Meta<typeof IconButton>;

export default meta;
type Story = StoryObj<typeof IconButton>;

export const Basic: Story = {
  args: {
    name: 'back',
    color: '#2B2B2B',
    size: 20,
  },
  argTypes: {
    name: {
      control: { type: 'select' },
      options: iconNames,
    },
  },
  render: (args) => <IconButton {...args} />,
};
