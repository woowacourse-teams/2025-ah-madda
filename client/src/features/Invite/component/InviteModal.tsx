import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

import { useInviteOrganizationProcess } from '../hooks/useInviteOrganizationProcess';
import { useNickNameForm } from '../hooks/useNickNameForm';

import { OrganizationInfo } from './OrganizationInfo';

export const InviteModal = () => {
  const { nickname, handleNicknameChange } = useNickNameForm();
  const { organizationData, handleJoin, handleClose } = useInviteOrganizationProcess();

  // S.TODO : imgurl 처리
  return (
    <Modal
      isOpen={true}
      onClose={handleClose}
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
        <OrganizationInfo name={organizationData?.name ?? ''} />
        <Input
          autoFocus
          id="nickname"
          label=""
          type="text"
          min={10}
          placeholder="닉네임을 입력하세요"
          value={nickname}
          onChange={handleNicknameChange}
        />
      </Flex>
      <Flex gap="12px" alignItems="center">
        <Button variant="outline" size="full" onClick={handleClose}>
          취소
        </Button>
        <Button size="full" disabled={!nickname.trim()} onClick={() => handleJoin(nickname)}>
          참가하기
        </Button>
      </Flex>
    </Modal>
  );
};
