import { CheckBox } from '@/shared/components/CheckBox';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { Guest, NonGuest } from '../types';

import { GuestItem } from './GuestItem';

type GuestListProps = {
  title: string;
  titleColor: string;
  guests: Guest[] | NonGuest[];
  onGuestChecked: (organizationMemberId: number) => void;
  onAllGuestChecked: VoidFunction;
  onGuestClick?: (guest: Guest | NonGuest) => void;
};

export const GuestList = ({
  title,
  titleColor,
  guests,
  onGuestChecked,
  onAllGuestChecked,
  onGuestClick,
}: GuestListProps) => {
  const isAllChecked = guests.length > 0 && guests.every((guest) => guest.isChecked);

  return (
    <Flex dir="column" gap="16px">
      <Flex alignItems="center" gap="8px">
        {guests.length > 0 && (
          <CheckBox checked={isAllChecked} size="md" onClick={onAllGuestChecked} />
        )}
        <Text type="Label" weight="medium" color={titleColor}>
          {title}
        </Text>
      </Flex>

      <Flex dir="column" gap="12px">
        {guests.map((guest, index) => (
          <GuestItem
            key={index}
            guest={guest}
            onGuestChecked={onGuestChecked}
            onClick={onGuestClick}
          />
        ))}
      </Flex>
    </Flex>
  );
};
