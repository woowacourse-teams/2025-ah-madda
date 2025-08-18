import { ChangeEvent, useState } from 'react';

export const useMaxCapacity = (initialValue: number | string) => {
  const [maxCapacity, setMaxCapacity] = useState(initialValue);

  const handleMaxCapacityChange = (e: ChangeEvent<HTMLInputElement>) => {
    const numberValue = Number(e.target.value);

    if (!Number.isInteger(numberValue)) {
      alert('수용 인원은 숫자만 입력가능합니다.');
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
