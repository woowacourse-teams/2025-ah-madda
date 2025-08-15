import { Meta, StoryObj } from '@storybook/react';

import { Button } from '@/shared/components/Button';

import { ToastProvider, useToast } from './ToastContext';

const meta = {
  title: 'components/Toast',
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'Toast system using context. Call `useToast().success()` or `useToast().error()` to trigger.',
      },
    },
    layout: 'fullscreen',
  },
} satisfies Meta;

export default meta;
type Story = StoryObj;

const ToastStoryExample = () => {
  const { success, error } = useToast();

  return (
    <div style={{ padding: '2rem', display: 'flex', gap: '0.75rem' }}>
      <Button onClick={() => success('성공 토스트!')}>Success</Button>
      <Button onClick={() => error('에러 토스트!')}>Error</Button>
      <Button onClick={() => success('5초 동안 표시!', { duration: 5000 })}>Success (5s)</Button>
    </div>
  );
};

export const Basic: Story = {
  render: () => (
    <ToastProvider>
      <ToastStoryExample />
    </ToastProvider>
  ),
};
