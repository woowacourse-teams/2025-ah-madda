import { useState } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { Toast } from './Toast';

const meta = {
  title: 'components/Toast',
  component: Toast,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'Toast component to notify users of success or error events. Automatically disappears after a given duration.',
      },
    },
    layout: 'fullscreen',
  },
} satisfies Meta<typeof Toast>;

export default meta;
type Story = StoryObj<typeof Toast>;

const ToastExample = ({
  message,
  duration,
  variant,
}: {
  message: string;
  duration?: number;
  variant?: 'success' | 'error';
}) => {
  const [show, setShow] = useState(true);

  return (
    <div style={{ minHeight: '300px', position: 'relative' }}>
      <button onClick={() => setShow(true)}>Show Toast</button>
      {show && (
        <Toast
          message={message}
          duration={duration}
          variant={variant}
          onClose={() => setShow(false)}
        />
      )}
    </div>
  );
};

export const Basic: Story = {
  args: {
    message: '이벤트 생성에 실패했어요.',
    duration: 3000,
    variant: 'error',
  },
  render: (args) => <ToastExample {...args} />,
};
