import { useState } from 'react';

type CheckableGuest = {
  organizationMemberId: number;
  nickname: string;
};
export const useCheckableGuests = <T extends CheckableGuest>(guests: T[]) => {
  const [checkedIds, setCheckedIds] = useState<Set<number>>(new Set());

  const toggleAll = () => {
    setCheckedIds((prev) => {
      if (prev.size === guests.length) {
        return new Set();
      }
      return new Set(guests.map((guest) => guest.organizationMemberId));
    });
  };

  const toggleItem = (organizationMemberId: number) => {
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

  const guestData = guests.map((guest) => ({
    ...guest,
    isChecked: checkedIds?.has(guest.organizationMemberId) || false,
  }));
  const getCheckedGuests = () => guestData.filter((guest) => guest.isChecked);

  return {
    guestData,
    toggleAll,
    toggleItem,
    getCheckedGuests,
  };
};
