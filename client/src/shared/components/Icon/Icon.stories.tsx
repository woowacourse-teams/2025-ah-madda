import type { Meta, StoryObj } from '@storybook/react';

import { iconNames } from './assets';
import { Icon } from './Icon';

const meta = {
  title: 'components/Icon',
  component: Icon,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'The Icon component displays visual symbols to represent actions, statuses, or content in a compact, customizable form.',
      },
    },
  },
} satisfies Meta<typeof Icon>;

export default meta;
type Story = StoryObj<typeof Icon>;

export const Basic: Story = {
  args: {
    name: 'users',
    size: 24,
    color: 'red',
  },
  argTypes: {
    name: {
      control: {
        type: 'select',
        options: iconNames,
      },
    },
    size: {
      control: {
        type: 'number',
      },
      defaultValue: 24,
    },
    color: {
      control: {
        type: 'color',
      },
      defaultValue: '#2B2B2B',
    },
  },
  render: (args) => <Icon {...args} />,
};
