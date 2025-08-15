import { UNLIMITED_CAPACITY } from '../../New/constants/errorMessages';

export const calculateCapacityStatus = (maxCapacity: number, currentGuestCount: number) => {
  const isUnlimited = maxCapacity === UNLIMITED_CAPACITY;
  const progressValue = isUnlimited ? 1 : Number(currentGuestCount);
  const progressMax = isUnlimited ? 1 : maxCapacity;
  return {
    isUnlimited,
    progressValue,
    progressMax,
  };
};
