import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { Guest } from '../types';

import { GuestItem } from './GuestItem';
import { type GuestVariant } from './GuestItem.styled';

type GuestListProps = {
  title: string;
  titleColor: string;
  guests: Guest[];
  variant: GuestVariant;
  onGuestClick: (guest: Guest) => void;
};

export const GuestList = ({ title, titleColor, guests, variant, onGuestClick }: GuestListProps) => {
  return (
    <Flex dir="column" gap="16px">
      <Text type="Body" weight="medium" color={titleColor}>
        {title}
      </Text>

      <Flex dir="column" gap="12px">
        {guests.map((guest) => (
          <GuestItem
            key={crypto.randomUUID()}
            guest={guest}
            onClick={onGuestClick}
            variant={variant}
          />
        ))}
      </Flex>
    </Flex>
  );
};
