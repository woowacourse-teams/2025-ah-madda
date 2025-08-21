import { ChangeEvent, useState } from 'react';

import { useToast } from '@/shared/components/Toast/ToastContext';

const MAX_CAPACITY = 100_000_000;

export const useMaxCapacity = (initialValue: number | string) => {
  const { error } = useToast();
  const [maxCapacity, setMaxCapacity] = useState(initialValue);

  const handleMaxCapacityChange = (e: ChangeEvent<HTMLInputElement>) => {
    const numberValue = Number(e.target.value);

    if (!Number.isInteger(numberValue)) {
      error('수용 인원은 숫자만 입력가능해요.');
      setMaxCapacity(Number(maxCapacity));
      return;
    }

    if (numberValue > MAX_CAPACITY) {
      error(`최대 ${MAX_CAPACITY.toLocaleString('ko-KR')}명까지 입력할 수 있어요.`);
      return;
    }

    setMaxCapacity(Number(numberValue));
  };

  return {
    maxCapacity,
    handleMaxCapacityChange,
  };
};
