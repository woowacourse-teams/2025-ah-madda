import { useState } from 'react';

type CheckableGuest = {
  organizationMemberId: number;
  nickname: string;
};
export const useCheckedGuest = <T extends CheckableGuest>(initialGuests: T[]) => {
  const [checkedIds, setCheckedIds] = useState<Set<number>>();

  const handleAllChecked = () => {
    setCheckedIds((prev) => {
      const newSet = new Set(prev);
      if (newSet.size === initialGuests.length) {
        return new Set();
      }
      return new Set(initialGuests.map((guest) => guest.organizationMemberId));
    });
  };

  const handleGuestChecked = (organizationMemberId: number) => {
    setCheckedIds((prev) => {
      const newSet = new Set(prev);
      if (newSet.has(organizationMemberId)) {
        newSet.delete(organizationMemberId);
      } else {
        newSet.add(organizationMemberId);
      }
      return newSet;
    });
  };

  const guestData = initialGuests.map((guest) => ({
    ...guest,
    isChecked: checkedIds?.has(guest.organizationMemberId) || false,
  }));

  return {
    guestData,
    handleAllChecked,
    handleGuestChecked,
  };
};
