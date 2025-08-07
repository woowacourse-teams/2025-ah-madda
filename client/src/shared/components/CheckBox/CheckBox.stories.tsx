import type { Meta, StoryObj } from '@storybook/react';

import { Flex } from '../Flex';

import { CheckBox } from './CheckBox';

const meta = {
  title: 'components/CheckBox',
  component: CheckBox,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'The CheckBox component is a customizable checkbox that can be checked or unchecked, with different sizes available.',
      },
    },
  },
} satisfies Meta<typeof CheckBox>;

export default meta;
type Story = StoryObj<typeof CheckBox>;

export const Basic: Story = {
  args: {
    checked: true,
    size: 'md',
  },
  argTypes: {
    checked: {
      control: 'boolean',
    },
    size: {
      control: 'select',
      options: ['sm', 'md', 'lg'],
    },
  },
  render: (args) => <CheckBox {...args} />,
};

export const ALL: Story = {
  render: () => {
    return (
      <Flex>
        <CheckBox checked={true} size="sm" />
        <CheckBox checked={true} size="md" />
        <CheckBox checked={true} size="lg" />
      </Flex>
    );
  },
};
