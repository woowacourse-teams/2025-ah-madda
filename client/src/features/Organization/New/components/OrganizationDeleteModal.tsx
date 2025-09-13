import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type OrganizationDeleteModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onDeleteConfirm: () => void;
};

export const OrganizationDeleteModal = ({
  isOpen,
  onClose,
  onDeleteConfirm,
}: OrganizationDeleteModalProps) => {
  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        max-width: 400px;
      `}
    >
      <Flex dir="column" gap="24px" padding="24px">
        <Flex dir="column" gap="8px">
          <Text type="Heading" weight="bold" color={theme.colors.gray900}>
            이벤트 스페이스 삭제
          </Text>
          <Flex dir="column">
            <Text type="Body" weight="regular" color={theme.colors.gray600}>
              이벤트 스페이스를 삭제하시겠습니까?
            </Text>
            <Text type="Body" weight="regular" color={theme.colors.gray600}>
              삭제된 이벤트 스페이스는 복구할 수 없습니다.
            </Text>
          </Flex>
        </Flex>

        <Flex dir="row" gap="8px" justifyContent="flex-end">
          <Button variant="outline" color="secondary" size="md" onClick={onClose}>
            취소
          </Button>
          <Button
            variant="solid"
            color="primary"
            size="md"
            onClick={onDeleteConfirm}
            css={css`
              background-color: ${theme.colors.red100};
              color: ${theme.colors.red400};

              &:hover {
                background-color: ${theme.colors.red300};
                color: ${theme.colors.white};
              }
            `}
          >
            삭제
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
