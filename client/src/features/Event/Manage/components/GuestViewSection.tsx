import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

import { Guest } from '../types';

import { GuestList } from './GuestList';

type GuestViewSectionProps = {
  completedGuests: Guest[];
  pendingGuests: Guest[];
  onGuestClick: (guest: Guest) => void;
};

export const GuestViewSection = ({
  completedGuests,
  pendingGuests,
  onGuestClick,
}: GuestViewSectionProps) => {
  return (
    <Card>
      <Flex as="section" dir="column" gap="20px">
        <Flex alignItems="center" gap="8px">
          <Icon name="users" size={14} color="#4A5565" />
          <Text type="caption" weight="regular" color="#4A5565">
            게스트 조회
          </Text>
        </Flex>

        <GuestList
          title={`신청 완료 (${completedGuests.length}명)`}
          titleColor="#00A63E"
          guests={completedGuests}
          variant="completed"
          onGuestClick={onGuestClick}
        />

        <GuestList
          title={`미신청 (${pendingGuests.length}명)`}
          titleColor="#4A5565"
          guests={pendingGuests}
          variant="pending"
          onGuestClick={onGuestClick}
        />
      </Flex>
    </Card>
  );
};
