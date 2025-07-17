import { useState } from 'react';

import type { Meta, StoryObj } from '@storybook/react';

import { Modal } from './Modal';

const meta = {
  title: 'components/Modal',
  component: Modal,
  tags: ['autodocs'],
  parameters: {
    docs: {
      description: {
        component:
          'A customizable modal component with flexible content injection, backdrop click, close button, and focus trap support.',
      },
    },
  },
} satisfies Meta<typeof Modal>;

export default meta;
type Story = StoryObj<typeof Modal>;

const ModalExample = (args: any) => {
  const [open, setOpen] = useState(true);

  return (
    <>
      <button onClick={() => setOpen(true)}>Open Modal</button>
      <Modal {...args} isOpen={open} onClose={() => setOpen(false)}>
        <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={{ margin: 0 }}>회원 탈퇴</h2>
        </header>
        <div style={{ marginTop: '16px' }}>정말 탈퇴하시겠어요? 😢</div>
        <footer
          style={{ marginTop: '120px', display: 'flex', justifyContent: 'center', gap: '8px' }}
        >
          <button onClick={() => setOpen(false)}>취소</button>
          <button>확인</button>
        </footer>
      </Modal>
    </>
  );
};

export const Basic: Story = {
  render: (args) => (
    <div style={{ minHeight: '30vh' }}>
      <ModalExample {...args} />
    </div>
  ),
  args: {
    hasCloseButton: true,
    hasBackdropClick: true,
    position: 'center',
    size: 'small',
  },
};

export const WithoutBackdropClick: Story = {
  render: (args) => (
    <div style={{ minHeight: '30vh' }}>
      <ModalExample {...args} />
    </div>
  ),
  args: {
    hasCloseButton: true,
    hasBackdropClick: false,
    position: 'center',
    size: 'small',
  },
};

export const WithoutCloseButton: Story = {
  render: (args) => (
    <div style={{ minHeight: '30vh' }}>
      <ModalExample {...args} />
    </div>
  ),
  args: {
    hasCloseButton: false,
    hasBackdropClick: true,
    position: 'center',
    size: 'small',
  },
};

export const LargeBottomModal: Story = {
  render: (args) => (
    <div style={{ minHeight: '30vh' }}>
      <ModalExample {...args} />
    </div>
  ),
  args: {
    hasCloseButton: true,
    hasBackdropClick: true,
    position: 'bottom',
    size: 'large',
  },
};
