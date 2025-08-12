import type { Meta, StoryObj } from '@storybook/react';

import { Textarea } from './Textarea';

const meta = {
  title: 'components/Textarea',
  component: Textarea,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A customizable textarea component with label, helper text, required mark, and support for native textarea attributes.',
      },
    },
  },
} satisfies Meta<typeof Textarea>;

export default meta;
type Story = StoryObj<typeof Textarea>;

export const Basic: Story = {
  args: {
    id: 'description',
    label: '설명',
    name: 'description',
    placeholder: '설명을 입력하세요',
    errorMessage: '설명은 필수 입력 항목입니다.',
    value: '',
    onChange: () => {},
  },
};

export const WithHelperText: Story = {
  args: {
    id: 'bio',
    label: '자기소개',
    name: 'bio',
    placeholder: '간단한 자기소개를 작성해주세요',
    helperText: '최대 200자까지 작성 가능합니다.',
    value: '',
    onChange: () => {},
  },
};

export const Required: Story = {
  args: {
    id: 'feedback',
    label: '피드백',
    name: 'feedback',
    isRequired: true,
    placeholder: '의견을 입력하세요',
    value: '',
    onChange: () => {},
  },
};
