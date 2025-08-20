import { useEffect } from 'react';

import { css } from '@emotion/react';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';
import { removeLocalStorage, setLocalStorage } from '@/shared/utils/localStorage';

import { useInviteOrganizationProcess } from '../hooks/useInviteOrganizationProcess';
import { useNickNameForm } from '../hooks/useNickNameForm';

import { OrganizationInfo } from './OrganizationInfo';

export const InviteModal = () => {
  const { nickname, handleNicknameChange } = useNickNameForm();
  const { organizationData, handleJoin, handleClose, inviteCode } = useInviteOrganizationProcess();

  const handleGoogleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };

  useEffect(() => {
    if (inviteCode) {
      setLocalStorage('inviteCode', inviteCode);
    }
    return () => {
      removeLocalStorage('inviteCode');
    };
  }, [inviteCode]);

  // S.TODO : imgurl 처리
  return (
    <Modal
      isOpen={true}
      onClose={handleClose}
      css={css`
        width: 380px;
      `}
      showCloseButton={false}
    >
      {isAuthenticated() ? (
        <>
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
        </>
      ) : (
        <Flex dir="column" gap="24px" alignItems="center">
          <Text type="Title" weight="medium" color={theme.colors.gray900}>
            로그인이 필요한 서비스입니다.
          </Text>
          <Button size="full" onClick={handleGoogleLogin}>
            로그인
          </Button>
        </Flex>
      )}
    </Modal>
  );
};
