import type { Meta, StoryObj } from '@storybook/react';

import { Button } from '../Button';
import { Flex } from '../Flex';
import { Icon } from '../Icon';

import { Header } from './Header';

const meta = {
  title: 'components/Header',
  component: Header,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'The Header component is a customizable header that can contain a left and right element.',
      },
    },
  },
} satisfies Meta<typeof Header>;

export default meta;
type Story = StoryObj<typeof Header>;

export const Basic: Story = {
  render: () => (
    <Flex justifyContent="center" alignItems="center" height="30vh">
      <Header left={<Icon name="logo" />} />
    </Flex>
  ),
};

export const LeftAndRight: Story = {
  args: {
    left: <Icon name="logo" />,
    right: <Button size="sm">로그아웃</Button>,
  },
  render: (args) => <Header {...args} />,
};
