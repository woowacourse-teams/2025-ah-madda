import type { Meta, StoryObj } from '@storybook/react';

import { Test } from './Test';

const meta = {
  title: 'shared/components/Test',
  component: Test,
  parameters: {
    layout: 'centered',
  },
  tags: ['autodocs'],
  argTypes: {
    variant: {
      control: { type: 'select' },
      options: ['primary', 'secondary'],
    },
    title: {
      control: { type: 'text' },
    },
    children: {
      control: { type: 'text' },
    },
  },
} satisfies Meta<typeof Test>;

export default meta;
type Story = StoryObj<typeof meta>;

// 기본 스토리
export const Primary: Story = {
  args: {
    title: 'Primary Test',
    variant: 'primary',
  },
};

// Secondary 변형
export const Secondary: Story = {
  args: {
    title: 'Secondary Test',
    variant: 'secondary',
  },
};

// 자식 요소 포함
export const WithChildren: Story = {
  args: {
    title: 'Test with Children',
    variant: 'primary',
    children: '이것은 테스트 컴포넌트의 자식 요소입니다. 다양한 내용을 넣을 수 있어요!',
  },
};

// 긴 제목
export const LongTitle: Story = {
  args: {
    title: 'Very Long Title That Tests How The Component Handles Longer Text Content',
    variant: 'secondary',
    children:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit. Sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
  },
};
