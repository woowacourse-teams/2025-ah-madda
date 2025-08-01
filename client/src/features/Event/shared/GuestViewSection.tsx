import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';

import { GuestList } from '../Manage/components/GuestList';
import { PreQuestionModal } from '../Manage/components/PreQuestionModal';
import { Guest, NonGuest } from '../Manage/types';

type GuestViewSectionProps = {
  guests: Guest[];
  nonGuests: NonGuest[];
  variant?: 'detail' | 'manage';
};

export const GuestViewSection = ({
  guests,
  nonGuests,
  variant = 'detail',
}: GuestViewSectionProps) => {
  const { isOpen, open, close } = useModal(false);

  const handleGuestClick = (guest: Guest | NonGuest) => {
    if (variant === 'manage' && 'guestId' in guest) {
      open();
    }
  };

  return (
    <>
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
            onGuestClick={variant === 'manage' ? handleGuestClick : undefined}
          />

          <GuestList
            title={`미신청 (${nonGuests.length}명)`}
            titleColor="#4A5565"
            guests={nonGuests}
            onGuestClick={variant === 'manage' ? handleGuestClick : undefined}
          />
        </Flex>
      </Card>

      {variant === 'manage' && <PreQuestionModal isOpen={isOpen} onClose={close} />}
    </>
  );
};
