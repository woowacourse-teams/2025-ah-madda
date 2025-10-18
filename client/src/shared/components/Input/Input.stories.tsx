import { ComponentProps, useState } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { Flex } from '../Flex';

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

const DefaultInput = (args: ComponentProps<typeof Input>) => {
  const [value, setValue] = useState('');
  const [error, setError] = useState('');
  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValue(e.target.value);
    setError(e.target.value.length > 10 ? '이름은 10글자까지만 가능합니다.' : '');
  };
  const handleClear = () => {
    setValue('');
    setError('');
  };

  return (
    <Input
      value={value}
      onChange={handleChange}
      errorMessage={error}
      onClear={handleClear}
      {...args}
    />
  );
};

export const Basic: Story = {
  args: {
    id: 'name',
    name: 'name',
    type: 'text',
    placeholder: '이름을 입력하세요',
    maxLength: 10,
    showCounter: true,
  },
  render: (args) => {
    return <DefaultInput {...args} />;
  },
};

export const WithClearButton: Story = {
  args: {
    id: 'email',
    name: 'email',
    type: 'text',
    placeholder: '이메일을 입력하세요',
    value: 'test',
  },
  render: (args) => <WithValueExample {...args} />,
};

const WithValueExample = (args: ComponentProps<typeof Input>) => {
  const [value, setValue] = useState('');
  return (
    <Flex>
      <Input
        {...args}
        value={value}
        onChange={(e) => setValue(e.target.value)}
        onClear={() => setValue('')}
      />
    </Flex>
  );
};

export const WithHelperText: Story = {
  args: {
    id: 'password',
    name: 'password',
    placeholder: '8자 이상 입력해주세요',
    helperText: '대소문자, 숫자, 특수문자를 포함해주세요',
    type: 'password',
  },
  render: (args) => <WithValueExample {...args} />,
};

export const WithErrorMessage: Story = {
  args: {
    id: 'password',
    name: 'password',
    placeholder: '8자 이상 입력해주세요',
    errorMessage: '대소문자, 숫자, 특수문자를 포함해주세요',
    type: 'password',
    value: '',
    onChange: () => {},
  },
  render: (args) => <DefaultInput {...args} disabled />,
};
