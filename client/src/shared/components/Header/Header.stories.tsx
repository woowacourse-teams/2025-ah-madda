import type { Meta, StoryObj } from '@storybook/react';

import { Header } from './Header';

const meta = {
  title: 'components/Header',
  component: Header,
  tags: ['autodocs'],
} satisfies Meta<typeof Header>;

export default meta;
type Story = StoryObj<typeof Header>;

export const LeftOnly: Story = {
  args: {
    left: <h2 style={{ margin: 0 }}>아맞다!</h2>,
  },
  render: (args) => <Header {...args} />,
};

export const LeftAndRight: Story = {
  args: {
    left: <h2 style={{ margin: 0 }}>아맞다!</h2>,
    right: (
      <div style={{ display: 'flex', gap: '12px' }}>
        <button>로그아웃</button>
        <button>내 이벤트</button>
      </div>
    ),
  },
  render: (args) => <Header {...args} />,
};
