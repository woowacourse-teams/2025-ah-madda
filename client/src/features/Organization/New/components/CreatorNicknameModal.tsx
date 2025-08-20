import { css } from '@emotion/react';

import { OrganizationInfo } from '@/features/Invite/component/OrganizationInfo';
import { useNickNameForm } from '@/features/Invite/hooks/useNickNameForm';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

type CreatorNicknameModalProps = {
  isOpen: boolean;
  orgName: string;
  previewUrl?: string;
  isSubmitting?: boolean;
  onConfirm: (nickname: string) => void;
  onCancel: () => void;
};

export const CreatorNicknameModal = ({
  isOpen,
  orgName,
  previewUrl,
  isSubmitting,
  onConfirm,
  onCancel,
}: CreatorNicknameModalProps) => {
  const { nickname, handleNicknameChange } = useNickNameForm();

  const submit = () => onConfirm(nickname);

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
        <OrganizationInfo name={orgName} imageUrl={previewUrl} />
        <Input
          autoFocus
          id="nickname"
          type="text"
          placeholder="닉네임을 입력하세요"
          value={nickname}
          onChange={handleNicknameChange}
          showCounter
          onKeyDown={(e) => {
            if (e.key === 'Enter') {
              e.preventDefault();
              submit();
            }
          }}
        />
      </Flex>

      <Flex gap="12px" alignItems="center">
        <Button variant="outline" size="full" onClick={onCancel} disabled={isSubmitting}>
          취소
        </Button>
        <Button size="full" disabled={!nickname.trim() || isSubmitting} onClick={submit}>
          생성하기
        </Button>
      </Flex>
    </Modal>
  );
};
