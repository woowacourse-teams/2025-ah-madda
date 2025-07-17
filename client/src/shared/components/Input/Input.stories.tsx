import type { Meta, StoryObj } from '@storybook/react';

import { Input } from './Input';

const meta = {
  title: 'components/Input',
  component: Input,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A customizable input component with label, helper text, required mark, and support for native input attributes.',
      },
    },
  },
} satisfies Meta<typeof Input>;

export default meta;
type Story = StoryObj<typeof Input>;

export const Basic: Story = {
  args: {
    title: '이메일',
    name: 'email',
    placeholder: '이메일을 입력하세요',
    value: '',
    onChange: () => {},
  },
};

export const WithHelperText: Story = {
  args: {
    title: '비밀번호',
    name: 'password',
    placeholder: '8자 이상 입력해주세요',
    helperText: '대소문자, 숫자, 특수문자를 포함해주세요',
    type: 'password',
    value: '',
    onChange: () => {},
  },
};

export const Required: Story = {
  args: {
    title: '이름',
    name: 'name',
    required: true,
    placeholder: '이름을 입력하세요',
    value: '',
    onChange: () => {},
  },
};
