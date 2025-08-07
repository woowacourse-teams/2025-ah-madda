import { Meta, StoryObj } from '@storybook/react';

import { Button } from '@/shared/components/Button';

import { ToastProvider, useToast } from './ToastContext';

const meta = {
  title: 'components/Toast',
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component: 'Toast system using context. Call `useToast().openToast()` to trigger.',
      },
    },
    layout: 'fullscreen',
  },
} satisfies Meta;

export default meta;
type Story = StoryObj;

const ToastStoryExample = () => {
  const toast = useToast();

  return (
    <div style={{ padding: '2rem' }}>
      <Button
        onClick={() =>
          toast.openToast({
            message: '스토리북에서도 잘 작동해요!',
            variant: 'success',
            duration: 2000,
          })
        }
      >
        Show Toast
      </Button>
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
