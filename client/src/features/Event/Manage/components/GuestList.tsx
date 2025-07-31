import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { Guest, NonGuest } from '../types';

import { GuestItem } from './GuestItem';

type GuestListProps = {
  title: string;
  titleColor: string;
  guests: Guest[] | NonGuest[];
};

export const GuestList = ({ title, titleColor, guests }: GuestListProps) => {
  return (
    <Flex dir="column" gap="16px">
      <Text type="Label" weight="medium" color={titleColor}>
        {title}
      </Text>

      <Flex dir="column" gap="12px">
        {guests.map((guest, index) => (
          <GuestItem key={index} guest={guest} />
        ))}
      </Flex>
    </Flex>
  );
};
