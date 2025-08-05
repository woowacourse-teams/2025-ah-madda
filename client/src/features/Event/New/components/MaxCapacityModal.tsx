import { useEffect, useRef, useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

type Props = {
  isOpen: boolean;
  initialValue: number | string;
  onClose: () => void;
  onSubmit: (value: number) => void;
};

export const MaxCapacityModal = ({ isOpen, initialValue, onClose, onSubmit }: Props) => {
  const [value, setValue] = useState(initialValue.toString());
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    if (isOpen) {
      setTimeout(() => {
        inputRef.current?.focus();
      }, 0);
    }
  }, [isOpen]);

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
    <Modal isOpen={isOpen} onClose={onClose} showCloseButton={false}>
      <Flex dir="column" padding="24px" gap="20px" width="300px">
        <Text type="Title" weight="bold">
          최대 수용 인원
        </Text>

        <Input
          id="maxCapacityInput"
          label="수용 인원"
          type="text"
          ref={inputRef}
          value={value}
          onChange={(e) => {
            const raw = e.target.value;

            if (/^\d*$/.test(raw)) {
              setValue(raw);
            }
          }}
        />

        <Flex gap="10px" justifyContent="flex-end">
          <Button onClick={onClose} size="sm" variant="outline">
            취소
          </Button>
          <Button color="tertiary" onClick={handleConfirm} size="sm">
            제한 설정
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};
