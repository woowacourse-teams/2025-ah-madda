import { css } from '@emotion/react';

import { OrganizationInfo } from '@/features/Invite/component/OrganizationInfo';
import { useNickNameForm } from '@/features/Invite/hooks/useNickNameForm';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

import { useCreateOrganizationProcess } from '../hooks/useCreateOrganizationProcess';

type Props = {
  isOpen: boolean;
  orgName: string;
  name: string;
  description: string;
  thumbnail: File | null;
  onSuccess?: () => void;
  onError?: (e: unknown) => void;
  onCancel: () => void;
};

export const CreatorNicknameModal = ({
  isOpen,
  orgName,
  name,
  description,
  thumbnail,
  onSuccess,
  onError,
  onCancel,
}: Props) => {
  const { nickname, handleNicknameChange } = useNickNameForm();
  const { handleCreate } = useCreateOrganizationProcess({
    name,
    description,
    thumbnail,
    onSuccess,
    onError,
  });

  if (!isOpen) return null;

  return (
    <Modal
      isOpen={isOpen}
      onClose={onCancel}
      css={css`
        width: 380px;
      `}
    >
      <Flex justifyContent="space-between" alignItems="baseline">
        <Text type="Heading" weight="bold" color="#333">
          닉네임 설정
        </Text>
      </Flex>

      <Flex dir="column" alignItems="center">
        <OrganizationInfo name={orgName} />
        <Input
          autoFocus
          id="nickname"
          type="text"
          placeholder="닉네임을 입력하세요"
          value={nickname}
          onChange={handleNicknameChange}
          maxLength={20}
          showCounter
          onKeyDown={(e) => {
            if (e.key === 'Enter' && nickname.trim()) {
              e.preventDefault();
              handleCreate(nickname.trim());
            }
          }}
        />
      </Flex>

      <Flex gap="12px" alignItems="center">
        <Button variant="outline" size="full" onClick={onCancel}>
          취소
        </Button>
        <Button
          size="full"
          disabled={!nickname.trim()}
          onClick={() => handleCreate(nickname.trim())}
        >
          생성하기
        </Button>
      </Flex>
    </Modal>
  );
};
