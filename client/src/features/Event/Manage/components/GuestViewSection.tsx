import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

import { Guest, NonGuest } from '../types';

import { GuestList } from './GuestList';

type GuestViewSectionProps = {
  guests: Guest[];
  onGuestChecked: (organizationMemberId: number) => void;
  onAllChecked: VoidFunction;
  nonGuests: NonGuest[];
  onNonGuestChecked: (organizationMemberId: number) => void;
  onNonGuestAllChecked: VoidFunction;
};

export const GuestViewSection = ({
  guests,
  onGuestChecked,
  onAllChecked,
  nonGuests,
  onNonGuestChecked,
  onNonGuestAllChecked,
}: GuestViewSectionProps) => {
  return (
    <Card>
      <Flex as="section" dir="column" gap="20px">
        <Flex alignItems="center" gap="8px">
          <Icon name="user" size={18} />
          <Text type="Body" weight="regular" color="#4A5565">
            게스트 조회
          </Text>
        </Flex>

        <GuestList
          title={`신청 완료 (${guests.length}명)`}
          titleColor="#00A63E"
          guests={guests}
          onGuestChecked={onGuestChecked}
          onAllGuestChecked={onAllChecked}
        />
        <GuestList
          title={`미신청 (${nonGuests.length}명)`}
          titleColor="#4A5565"
          guests={nonGuests}
          onGuestChecked={onNonGuestChecked}
          onAllGuestChecked={onNonGuestAllChecked}
        />
      </Flex>
    </Card>
  );
};
