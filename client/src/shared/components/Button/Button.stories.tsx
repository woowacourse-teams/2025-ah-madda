import type { Meta, StoryObj } from '@storybook/react';

import { Button } from './Button';

const meta = {
  title: 'components/Button',
  component: Button,
  tags: ['autodocs'],
} satisfies Meta<typeof Button>;

export default meta;
type Story = StoryObj<typeof Button>;

export const Basic: Story = {
  args: {
    children: 'Button',
    width: '8rem',
    size: 'md',
    variant: 'filled',
    color: '#2563EB',
    fontColor: 'white',
  },
  render: (args) => <Button {...args} />,
};

export const Outlined: Story = {
  args: {
    width: '10rem',
    variant: 'outlined',
    color: '#808080',
    fontColor: '#000000',
    children: 'Outlined Button',
  },
  render: (args) => <Button {...args} />,
};

export const FullWidth: Story = {
  args: {
    children: 'Full Width Button',
    width: '100%',
    color: '#409869',
  },
  render: (args) => <Button {...args} />,
};
