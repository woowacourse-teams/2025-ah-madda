import type { Meta, StoryObj } from '@storybook/react';

import { Flex } from '../Flex';

import { Button } from './Button';

const meta = {
  title: 'components/Button',
  component: Button,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component: 'Button component is a styled button that can be used to trigger actions.',
      },
    },
  },
} satisfies Meta<typeof Button>;

export default meta;
type Story = StoryObj<typeof Button>;

export const Basic: Story = {
  args: {
    size: 'md',
    color: 'primary',
    variant: 'solid',
    disabled: false,
    children: 'Button',
  },
  argTypes: {
    type: { control: false },
  },

  render: (args) => <Button {...args} />,
};

export const Primary: Story = {
  args: {
    color: 'primary',
    children: 'Primary Button',
  },
  render: (args) => (
    <Flex dir="column" gap="10px">
      <Button size="sm" {...args}>
        Primary
      </Button>
      <Button size="md" {...args}>
        Primary
      </Button>
      <Button size="lg" {...args}>
        Primary
      </Button>
    </Flex>
  ),
};

export const Secondary: Story = {
  args: {
    color: 'secondary',
    children: 'Secondary Button',
  },
  render: (args) => (
    <Flex dir="column" gap="10px">
      <Button size="sm" {...args}>
        Second
      </Button>
      <Button size="md" {...args}>
        Secondary
      </Button>
      <Button size="lg" {...args}>
        Secondary
      </Button>
    </Flex>
  ),
};

export const Tertiary: Story = {
  args: {
    color: 'tertiary',
    children: 'Tertiary Button',
  },
  render: (args) => (
    <Flex dir="column" gap="10px">
      <Button size="sm" {...args}>
        Tertiary
      </Button>
      <Button size="md" {...args}>
        Tertiary
      </Button>
      <Button size="lg" {...args}>
        Tertiary
      </Button>
    </Flex>
  ),
};

export const Outlined: Story = {
  args: {
    variant: 'outline',
    children: 'Outlined',
  },
  render: (args) => <Button {...args} />,
};

export const FullWidth: Story = {
  args: {
    size: 'full',
    color: 'primary',
    children: 'Full Width Button',
  },
  render: (args) => <Button {...args} />,
};
