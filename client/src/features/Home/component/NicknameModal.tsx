import { useState } from 'react';

import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { useCreateProfile } from '@/api/mutations/useCreateProfile';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { IconButton } from '@/shared/components/IconButton';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';

type NicknameModalProps = {
  isOpen: boolean;
  onClose: () => void;
};

export const NicknameModal = ({ isOpen, onClose }: NicknameModalProps) => {
  //E.TODO 추후 organizationId 받아오기
  const createProfileMutation = useCreateProfile(1);
  const navigate = useNavigate();
  const [nickname, setNickname] = useState('');

  const handleNicknameSubmit = (nickname: string) => {
    createProfileMutation.mutate(
      { nickname },
      {
        onSuccess: () => {
          alert('조직 참가가 완료되었습니다!');
          onClose();
          navigate('/event');
        },
        onError: () => {
          alert('참가 중 오류가 발생했습니다. 다시 시도해 주세요.');
        },
      }
    );
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
    <Modal
      isOpen={isOpen}
      onClose={handleClose}
      showCloseButton={false}
      css={css`
        width: 400px;
      `}
    >
      <Flex dir="column" gap="20px">
        <Flex justifyContent="space-between" alignItems="baseline">
          <Text type="Label" weight="bold" color="#333">
            닉네임 설정
          </Text>
          <IconButton name="close" onClick={handleClose} />
        </Flex>

        <Flex dir="column">
          <Text type="Label" weight="regular" color="#666">
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
        <Flex gap="12px" alignItems="center">
          <Button
            variant="outlined"
            size="md"
            onClick={handleClose}
            color="#18A0FB"
            fontColor="#18A0FB"
            width="100%"
          >
            취소
          </Button>
          <Button
            variant="filled"
            size="md"
            onClick={handleSubmit}
            disabled={!nickname.trim()}
            width="100%"
          >
            참가하기
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
