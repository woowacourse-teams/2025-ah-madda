import { ChangeEvent, useState } from 'react';

import { useToast } from '@/shared/components/Toast/ToastContext';

const MAX_CAPACITY = 100_000_000;

export const useMaxCapacity = (initialValue: number | string) => {
  const { error } = useToast();
  const [maxCapacity, setMaxCapacity] = useState(initialValue);

  const handleMaxCapacityChange = (e: ChangeEvent<HTMLInputElement>) => {
    const numberValue = Number(e.target.value);

    if (!Number.isInteger(numberValue)) {
      error('수용 인원은 숫자만 입력가능합니다.');
      setMaxCapacity(Number(maxCapacity));
      return;
    }

    if (numberValue >= MAX_CAPACITY) {
      error('수용 인원이 너무 큽니다. 더 작은 값을 입력해주세요.');
      return;
    }

    setMaxCapacity(Number(numberValue));
  };

  return {
    maxCapacity,
    handleMaxCapacityChange,
  };
};
