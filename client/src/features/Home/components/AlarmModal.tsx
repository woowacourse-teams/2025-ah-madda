import { css } from '@emotion/react';

import { NotificationButton } from '@/features/Event/My/components/NotificationButton';
import { Flex } from '@/shared/components/Flex';
import { Modal, ModalProps } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';

export const AlarmModal = ({ isOpen, onClose }: ModalProps) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 350px;
      `}
    >
      <Flex width="100%" dir="column" justifyContent="center" alignItems="center">
        <Flex width="100%" dir="column" alignItems="center" gap="16px" margin="20px 0 0 0">
          <Text type="Heading" weight="semibold">
            알림을 허용하시겠습니까?
          </Text>
          <NotificationButton onClose={onClose} />
        </Flex>
      </Flex>
    </Modal>
  );
};
