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
    id: 'email',
    name: 'email',
    placeholder: '이메일을 입력하세요',
    errorMessage: '이메일 형식이 올바르지 않아요',
    value: '',
    onChange: () => {},
  },
};

export const WithHelperText: Story = {
  args: {
    id: 'password',
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
    id: 'name',
    name: 'name',
    isRequired: true,
    placeholder: '이름을 입력하세요',
    value: '',
    onChange: () => {},
  },
};
