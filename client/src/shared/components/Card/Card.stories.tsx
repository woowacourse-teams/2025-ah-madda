import type { Meta, StoryObj } from '@storybook/react';

import { Card } from './Card';

const meta = {
  title: 'components/Card',
  component: Card,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'Card component is a styled container that can be used to display content in a card-like format.',
      },
    },
  },
} satisfies Meta<typeof Card>;

export default meta;
type Story = StoryObj<typeof Card>;

export const Basic: Story = {
  args: {
    children: 'Card Component',
  },
  argTypes: {
    children: { control: 'text' },
  },
  render: (args) => <Card {...args} />,
};
