import type { Meta, StoryObj } from '@storybook/react';

import { Loading } from './Loading';

const meta = {
  title: 'components/Loading',
  component: Loading,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'The Loading component provides a visual indication that content is being loaded, enhancing user experience during wait times.',
      },
    },
  },
} satisfies Meta<typeof Loading>;

export default meta;
type Story = StoryObj<typeof Loading>;

export const Basic: Story = {
  args: {
    type: 'text',
    size: 48,
    color: 'gray',
  },
  argTypes: {
    type: {
      control: {
        type: 'select',
        options: ['text', 'spinner'],
      },
    },
    size: {
      control: {
        type: 'number',
      },
    },
    color: {
      control: { type: 'select' },
      options: ['primary', 'primary500', 'secondary', 'gray', 'gray500', 'red', 'white'],
    },
  },
  render: (args) => <Loading {...args} />,
};
