import { css } from '@emotion/react';

import { getGoogleAuthUrl } from '@/api/auth';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { ModalProps } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type LoginModalProps = {
  isOpen: boolean;
  onClose: () => void;
} & ModalProps;

export const LoginModal = ({ isOpen, onClose }: LoginModalProps) => {
  const handleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
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
      <Flex dir="column" gap="10px">
        <Text
          as="h1"
          type="Heading"
          weight="semibold"
          color={theme.colors.gray900}
          css={css`
            padding: 20px;
            text-align: center;
          `}
        >
          {`이벤트 신청은 로그인 후 이용할 수 있어요. \n먼저 로그인해 주세요.`}
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
