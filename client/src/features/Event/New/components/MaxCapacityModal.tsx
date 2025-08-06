import { useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

import { UNLIMITED_CAPACITY } from '../constants/errorMessages';

type Props = {
  isOpen: boolean;
  initialValue: number | string;
  onClose: () => void;
  onSubmit: (value: number) => void;
};

export const MaxCapacityModal = ({ isOpen, initialValue, onClose, onSubmit }: Props) => {
  const [value, setValue] = useState(initialValue.toString());

  const handleConfirm = () => {
    const parsed = Number(value);

    if (!Number.isInteger(parsed) || parsed < 1) {
      alert('수용 인원은 1 이상의 정수여야 합니다.');
      return;
    }

    onSubmit(parsed);
    onClose();
  };

  return (
    <Modal isOpen={isOpen} onClose={onClose}>
      <Flex dir="column" gap="20px" width="300px">
        <Text type="Heading" weight="bold">
          최대 수용 인원
        </Text>

        <Input
          id="maxCapacityInput"
          label="수용 인원을 입력해주세요."
          type="text"
          autoFocus
          value={value}
          onChange={(e) => {
            const raw = e.target.value;

            if (/^\d*$/.test(raw)) {
              setValue(raw);
            }
          }}
        />

        <Flex justifyContent="space-between" width="100%">
          <Button
            onClick={() => {
              onSubmit(UNLIMITED_CAPACITY);
              onClose();
            }}
            size="sm"
            color="gray"
          >
            무제한
          </Button>

          <Button onClick={handleConfirm} size="sm" color="black">
            설정
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
