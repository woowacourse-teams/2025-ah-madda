import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

import { useToast } from '../../../../shared/components/Toast/ToastContext';
import { UNLIMITED_CAPACITY } from '../constants/errorMessages';
import { useMaxCapacity } from '../hooks/useMaxCapacity';

type Props = {
  isOpen: boolean;
  initialValue: number | string;
  onClose: () => void;
  onSubmit: (value: number) => void;
};

export const MaxCapacityModal = ({ isOpen, initialValue, onClose, onSubmit }: Props) => {
  const { error } = useToast();
  const { maxCapacity, handleMaxCapacityChange } = useMaxCapacity(initialValue);

  const handleLimitedCapacity = () => {
    if (maxCapacity === 0) return error('수용 인원은 1명 이상이여야 합니다.');
    onSubmit(Number(maxCapacity));
    onClose();
  };

  const handleUnlimitedCapacity = () => {
    onSubmit(UNLIMITED_CAPACITY);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <Flex dir="column" gap="10px" width="380px">
        <Text type="Heading" weight="bold">
          최대 수용 인원
        </Text>

        <Input
          id="maxCapacityInput"
          type="text"
          autoFocus
          value={maxCapacity}
          onChange={handleMaxCapacityChange}
        />

        <Flex justifyContent="space-between" gap="12px">
          <Button onClick={handleUnlimitedCapacity} size="full" variant="outline">
            제한없음
          </Button>
          <Button onClick={handleLimitedCapacity} size="full" color="secondary">
            설정
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
