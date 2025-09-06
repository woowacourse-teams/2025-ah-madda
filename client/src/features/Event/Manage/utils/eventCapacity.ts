import { theme } from '@/shared/styles/theme';

import { UNLIMITED_CAPACITY } from '../../New/constants/errorMessages';

export type EventCapacityInfo = {
  isUnlimited: boolean;
  maxNumberOfGuests: string;
  progressValue: number;
  progressMax: number;
  progressColor: string;
};

export const getEventCapacityInfo = (
  maxCapacity: number,
  currentGuestCount: number
): EventCapacityInfo => {
  const isUnlimited = maxCapacity === UNLIMITED_CAPACITY;

  return {
    isUnlimited,
    maxNumberOfGuests: isUnlimited ? '제한없음' : `${maxCapacity}명`,
    progressValue: isUnlimited ? 1 : Number(currentGuestCount),
    progressMax: isUnlimited ? 1 : maxCapacity,
    progressColor: isUnlimited ? theme.colors.primary700 : theme.colors.primary500,
  };
};
