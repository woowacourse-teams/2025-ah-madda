import type { Meta, StoryObj } from '@storybook/react';

import { useModal } from '../../hooks/useModal';

import { Modal, ModalProps } from './Modal';

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

const ModalExample = (args: Partial<ModalProps>) => {
  const { isOpen, open, close } = useModal(true);

  return (
    <>
      <button onClick={open}>Open Modal</button>
      <Modal {...args} isOpen={isOpen} onClose={close}>
        <header style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
          <h2 style={{ margin: 0 }}>회원 탈퇴</h2>
        </header>
        <div style={{ marginTop: '16px' }}>정말 탈퇴하시겠어요? 😢</div>
        <footer
          style={{ marginTop: '120px', display: 'flex', justifyContent: 'center', gap: '8px' }}
        >
          <button onClick={close}>취소</button>
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
    showCloseButton: true,
    shouldCloseOnBackdropClick: true,
    size: 'sm',
  },
};

export const WithoutBackdropClick: Story = {
  render: (args) => (
    <div style={{ minHeight: '30vh' }}>
      <ModalExample {...args} />
    </div>
  ),
  args: {
    showCloseButton: true,
    shouldCloseOnBackdropClick: false,
    size: 'sm',
  },
};

export const WithoutCloseButton: Story = {
  render: (args) => (
    <div style={{ minHeight: '30vh' }}>
      <ModalExample {...args} />
    </div>
  ),
  args: {
    showCloseButton: false,
    shouldCloseOnBackdropClick: true,
    size: 'sm',
  },
};

export const LargeModal: Story = {
  render: (args) => (
    <div style={{ minHeight: '30vh' }}>
      <ModalExample {...args} />
    </div>
  ),
  args: {
    showCloseButton: true,
    shouldCloseOnBackdropClick: true,
    size: 'lg',
  },
};
