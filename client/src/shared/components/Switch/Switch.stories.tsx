import { useState, useEffect } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { Switch } from './Switch';

const meta = {
  title: 'components/Switch',
  component: Switch,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A modern toggle switch component for boolean states. Provides a sleek alternative to checkboxes with smooth animations and full accessibility support.',
      },
    },
  },
} satisfies Meta<typeof Switch>;

export default meta;
type Story = StoryObj<typeof Switch>;

const BasicExample = ({
  initialChecked,
  disabled,
}: {
  initialChecked: boolean;
  disabled: boolean;
}) => {
  const [checked, setChecked] = useState(initialChecked);

  useEffect(() => {
    setChecked(initialChecked);
  }, [initialChecked]);

  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
      <Switch checked={checked} disabled={disabled} onCheckedChange={setChecked} />
      <span style={{ fontSize: '14px', color: '#374151' }}>필수 질문</span>
    </div>
  );
};

export const Basic: Story = {
  args: {
    checked: false,
    disabled: false,
  },
  argTypes: {
    onCheckedChange: { action: 'toggled' },
  },
  render: (args) => (
    <BasicExample initialChecked={args.checked} disabled={args.disabled || false} />
  ),
};

export const Disabled: Story = {
  args: {
    checked: true,
    disabled: true,
  },
  argTypes: {
    onCheckedChange: { action: 'toggled' },
  },
  render: (args) => (
    <div style={{ display: 'flex', flexDirection: 'column', gap: '16px', maxWidth: '250px' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: '12px' }}>
        <Switch {...args} onCheckedChange={() => {}} />
        <span style={{ fontSize: '14px', color: '#9ca3af' }}>필수 질문</span>
      </div>
    </div>
  ),
};
