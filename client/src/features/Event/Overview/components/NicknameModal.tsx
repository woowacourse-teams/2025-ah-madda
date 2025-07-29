import { useState } from 'react';

import { useCreateProfile } from '@/api/mutations/useCreateProfile';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';

type NicknameModalProps = {
  isOpen: boolean;
  onClose: () => void;
};

export const NicknameModal = ({ isOpen, onClose }: NicknameModalProps) => {
  const createProfileMutation = useCreateProfile(1);
  const [nickname, setNickname] = useState('');

  const handleNicknameSubmit = (nickname: string) => {
    createProfileMutation.mutate(nickname, {
      onSuccess: () => {
        close();
        alert('조직 참가가 완료되었습니다!');
      },
      onError: () => {
        alert('참가 중 오류가 발생했습니다. 다시 시도해 주세요.');
      },
    });
  };

  const handleSubmit = () => {
    if (nickname.trim()) {
      handleNicknameSubmit(nickname.trim());
    }
  };

  const handleClose = () => {
    setNickname('');
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="md" css={{ minWidth: '360px' }}>
      <Flex dir="column" gap="20px">
        <Text type="Body" weight="bold" color="#333">
          닉네임 설정
        </Text>

        <Flex dir="column">
          <Text type="Body" weight="regular" color="#666">
            조직에서 사용할 닉네임을 입력해주세요.
          </Text>

          <Input
            id="nickname"
            label=""
            type="text"
            placeholder="닉네임을 입력하세요"
            value={nickname}
            onChange={(e) => setNickname(e.target.value)}
            autoFocus
          />
        </Flex>
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
          <Button variant="filled" size="md" onClick={handleSubmit} disabled={!nickname.trim()}>
            참가하기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
