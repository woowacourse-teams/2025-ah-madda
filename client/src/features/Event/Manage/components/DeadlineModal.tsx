import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal, ModalProps } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';

type DeadlineModalProps = {
  onDeadlineChange: VoidFunction;
} & ModalProps;

export const DeadlineModal = ({ isOpen, onClose, onDeadlineChange }: DeadlineModalProps) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 380px;
      `}
    >
      <Flex dir="column" gap="10px">
        <Text type="Heading" weight="bold">
          마감일 설정
        </Text>

        <Text type="Body" weight="medium">
          이벤트를 마감하시겠습니까?
        </Text>
        <Flex width="100%" gap="12px">
          <Button size="full" onClick={onClose} variant="outline">
            아니요
          </Button>
          <Button size="full" onClick={onDeadlineChange}>
            네
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
