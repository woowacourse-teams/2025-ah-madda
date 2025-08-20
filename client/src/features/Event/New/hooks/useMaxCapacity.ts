import { ChangeEvent, useState } from 'react';

import { useToast } from '@/shared/components/Toast/ToastContext';

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

    setMaxCapacity(Number(numberValue));
  };

  return {
    maxCapacity,
    handleMaxCapacityChange,
  };
};
