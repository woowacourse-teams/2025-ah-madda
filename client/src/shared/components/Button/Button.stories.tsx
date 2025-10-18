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
    isLoading: false,
    children: 'Button',
  },
  argTypes: {
    type: { control: false },
  },

  render: (args) => <Button {...args} />,
};

export const Colors: Story = {
  args: {
    children: 'Primary Button',
  },
  argTypes: {
    size: { control: false },
  },
  render: (args) => (
    <Flex dir="row" gap="24px" width="100%">
      <Flex dir="column" gap="10px" alignItems="flex-start">
        <Button {...args} size="sm">
          Primary
        </Button>
        <Button {...args} size="md">
          Primary
        </Button>
        <Button {...args} size="lg">
          Primary
        </Button>
      </Flex>
      <Flex dir="column" gap="10px" alignItems="flex-start">
        <Button {...args} color="secondary" size="sm">
          Secondary
        </Button>
        <Button {...args} color="secondary" size="md">
          Secondary
        </Button>
        <Button {...args} color="secondary" size="lg">
          Secondary
        </Button>
      </Flex>
      <Flex dir="column" gap="10px" alignItems="flex-start">
        <Button {...args} color="tertiary" size="sm">
          Tertiary
        </Button>
        <Button {...args} color="tertiary" size="md">
          Tertiary
        </Button>
        <Button {...args} color="tertiary" size="lg">
          Tertiary
        </Button>
      </Flex>
    </Flex>
  ),
};

export const Ghost: Story = {
  args: {
    variant: 'ghost',
    size: 'sm',
    children: 'Ghost',
  },
  argTypes: {
    size: { control: false },
  },
  render: (args) => <Button {...args} />,
};

export const Loading: Story = {
  args: {
    isLoading: true,
    children: 'Loading',
  },
  render: (args) => <Button {...args} />,
};

export const Outlined: Story = {
  args: {
    variant: 'outline',
    children: 'Outlined',
  },
  render: (args) => <Button {...args} />,
};

export const WithIcon: Story = {
  args: {
    iconName: 'plus',
    children: 'Icon Button',
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
