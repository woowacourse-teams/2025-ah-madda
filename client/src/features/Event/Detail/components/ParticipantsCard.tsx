import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Icon } from '../../../../shared/components/Icon';
import { ProgressBar } from '../../../../shared/components/ProgressBar';
import { Text } from '../../../../shared/components/Text';
import type { EventDetail } from '../../../Event/types/Event';
import { UNLIMITED_CAPACITY } from '../../New/constants/validation';

type ParticipantsCardProps = Pick<EventDetail, 'currentGuestCount' | 'maxCapacity'>;

export const ParticipantsCard = ({ currentGuestCount, maxCapacity }: ParticipantsCardProps) => {
  if (maxCapacity === UNLIMITED_CAPACITY) return null;

  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Flex gap="8px" alignItems="center">
          <Icon name="users" size={18} />
          <Text type="caption">참여 현황</Text>
        </Flex>
        <Flex dir="column" gap="8px">
          <Flex justifyContent="space-between">
            <Text type="caption">현재 신청자</Text>
            <Text type="caption">{`${currentGuestCount} / ${maxCapacity}명`}</Text>
          </Flex>
          <ProgressBar value={currentGuestCount} max={maxCapacity} color="black" />
        </Flex>
      </Flex>
    </Card>
  );
};
