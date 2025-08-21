import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

type TemplateDeleteModalProps = {
  isOpen: boolean;
  onClose: () => void;
  onDeleteConfirm: () => void;
};

export const TemplateDeleteModal = ({
  isOpen,
  onClose,
  onDeleteConfirm,
}: TemplateDeleteModalProps) => {
  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <Flex dir="column" gap="24px" padding="24px" width="400px">
        <Flex dir="column" gap="8px">
          <Text type="Heading" color={theme.colors.gray900}>
            템플릿 삭제
          </Text>
          <Text type="Body" color={theme.colors.gray600}>
            템플릿을 삭제하시겠습니까?
            <br />
            삭제된 템플릿은 복구할 수 없습니다.
          </Text>
        </Flex>

        <Flex dir="row" gap="8px" justifyContent="flex-end">
          <Button variant="outline" color="secondary" size="md" onClick={onClose}>
            취소
          </Button>
          <Button variant="solid" color="primary" size="md" onClick={onDeleteConfirm}>
            삭제
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
