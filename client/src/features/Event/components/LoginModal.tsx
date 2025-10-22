import { useEffect } from 'react';

import { css } from '@emotion/react';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { ModalProps } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

export const LoginModal = ({ isOpen, onClose }: ModalProps) => {
  const handleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };

  useEffect(() => {
    if (!isAuthenticated()) {
      open();
      sessionStorage.setItem('redirectAfterLogin', location.pathname);
    } else {
      close();
    }
  }, []);

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 380px;
        max-width: 100%;
      `}
    >
      <Flex dir="column" gap="10px" width="100%">
        <Text
          as="h1"
          type="Heading"
          weight="semibold"
          color={theme.colors.gray900}
          css={css`
            padding: 30px 0 20px 0;
            text-align: center;
          `}
        >
          {`로그인 후 이용할 수 있어요. \n먼저 로그인해 주세요.`}
        </Text>
        <Flex width="100%" gap="12px">
          <Button size="full" variant="outline" onClick={onClose}>
            취소
          </Button>
          <Button size="full" onClick={handleLogin}>
            로그인
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
