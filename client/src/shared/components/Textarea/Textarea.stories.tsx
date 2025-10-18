import { ComponentProps, useState } from 'react';

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

const DefaultTextarea = (args: ComponentProps<typeof Textarea>) => {
  const [value, setValue] = useState('');
  return <Textarea {...args} value={value} onChange={(e) => setValue(e.target.value)} />;
};

export const Basic: Story = {
  args: {
    id: 'description',
    name: 'description',
    placeholder: '설명을 입력하세요',
    maxLength: 200,
  },
  render: (args) => <DefaultTextarea {...args} />,
};

export const WithHelperText: Story = {
  args: {
    id: 'bio',
    name: 'bio',
    placeholder: '간단한 자기소개를 작성해주세요',
    helperText: '최대 200자까지 작성 가능합니다.',
  },
  render: (args) => <DefaultTextarea {...args} />,
};
