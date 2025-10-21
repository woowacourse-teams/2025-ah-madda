import { Avatar } from '@/shared/components/Avatar';
import { CheckBox } from '@/shared/components/CheckBox';
import { Flex } from '@/shared/components/Flex';

import { GuestItemContainer } from '../../containers/GuestItemContainer';
import { Guest, NonGuest } from '../../types';

type GuestItemProps = {
  onGuestChecked: (organizationMemberId: number) => void;
  guest: Guest | NonGuest;
  onGuestClick?: (guest: Guest | NonGuest) => void;
};

export const GuestItem = ({ guest, onGuestChecked, onGuestClick }: GuestItemProps) => {
  const isGuest = 'guestId' in guest;
  const variant = isGuest ? 'completed' : 'pending';

  const handleGuestClick = () => {
    if (onGuestClick && isGuest) {
      onGuestClick(guest);
    }
  };

  return (
    <GuestItemContainer variant={variant} clickable={isGuest} onClick={handleGuestClick}>
      <Flex gap="18px" alignItems="center" width="100%">
        <CheckBox
          checked={guest.isChecked}
          onChange={() => onGuestChecked(guest.organizationMemberId)}
        />
        <Avatar picture={null} name={guest.nickname} />
      </Flex>
    </GuestItemContainer>
  );
};
