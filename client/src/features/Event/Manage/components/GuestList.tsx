import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { Guest, NonGuest } from '../types';

import { GuestItem } from './GuestItem';

export type GuestVariant = 'completed' | 'pending';

type GuestListProps = {
  title: string;
  titleColor: string;
  guests: Guest[] | NonGuest[];
  variant: GuestVariant;
};

export const GuestList = ({ title, titleColor, guests, variant }: GuestListProps) => {
  return (
    <Flex dir="column" gap="16px">
      <Text type="caption" weight="medium" color={titleColor}>
        {title}
      </Text>

      <Flex dir="column" gap="12px">
        {guests.map((guest, index) => (
          <GuestItem key={index} guest={guest as Guest} variant={variant} />
        ))}
      </Flex>
    </Flex>
  );
};
