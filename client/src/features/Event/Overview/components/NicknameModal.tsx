import { useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';

type NicknameModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onSubmit: (nickname: string) => void;
  isLoading?: boolean;
};

export const NicknameModal = ({
  isOpen,
  onClose,
  onSubmit,
  isLoading = false,
}: NicknameModalProps) => {
  const [nickname, setNickname] = useState('');

  const handleSubmit = () => {
    if (nickname.trim()) {
      onSubmit(nickname.trim());
    }
  };

  const handleClose = () => {
    setNickname('');
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="md">
      <Flex dir="column" gap="20px">
        <Text type="Body" weight="bold" color="#333">
          닉네임 설정
        </Text>

        <Text type="Body" weight="regular" color="#666">
          조직에서 사용할 닉네임을 입력해주세요.
        </Text>

        <Input
          id="nickname"
          label="닉네임"
          type="text"
          placeholder="닉네임을 입력하세요"
          value={nickname}
          onChange={(e) => setNickname(e.target.value)}
          autoFocus
        />

        <Flex gap="12px" justifyContent="flex-end">
          <Button
            variant="outlined"
            size="md"
            onClick={handleClose}
            color="#18A0FB"
            fontColor="#18A0FB"
          >
            취소
          </Button>
          <Button
            variant="filled"
            size="md"
            onClick={handleSubmit}
            disabled={!nickname.trim() || isLoading}
          >
            참가하기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
