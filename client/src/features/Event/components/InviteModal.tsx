import { useState } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal, ModalProps } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';

type InviteModalProps = {
  onJoinOrganization: (inviteCode: string) => void;
  onSubmit: VoidFunction;
} & ModalProps;

export const InviteModal = ({
  isOpen,
  onClose,
  onJoinOrganization,
  onSubmit,
}: InviteModalProps) => {
  const [inviteCode, setInviteCode] = useState('');

  const handleInviteCodeChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setInviteCode(e.target.value);
  };

  const handleSubmit = () => {
    if (inviteCode.trim()) {
      onJoinOrganization(inviteCode);
      onSubmit();
    }
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 380px;
        max-width: 100%;
      `}
    >
      <Flex dir="column" gap="16px" width="100%">
        <Text type="Heading" weight="bold">
          초대 코드 입력
        </Text>
        <Input
          id="invite-code"
          type="text"
          value={inviteCode}
          onChange={handleInviteCodeChange}
          placeholder="초대 코드를 입력해주세요"
        />
      </Flex>
      <Flex gap="12px" alignItems="center">
        <Button size="full" variant="outline" onClick={onClose}>
          취소
        </Button>
        <Button size="full" onClick={handleSubmit}>
          입장하기
        </Button>
      </Flex>
    </Modal>
  );
};
