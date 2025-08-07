import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { ProgressBar } from '@/shared/components/ProgressBar';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import type { EventDetail } from '../../../Event/types/Event';
import { UNLIMITED_CAPACITY } from '../../New/constants/errorMessages';

type ParticipantsCardProps = Pick<EventDetail, 'currentGuestCount' | 'maxCapacity'>;

export const ParticipantsCard = ({ currentGuestCount, maxCapacity }: ParticipantsCardProps) => {
  const isUnlimited = maxCapacity === UNLIMITED_CAPACITY;
  const progressValue = isUnlimited ? 1 : currentGuestCount;
  const progressMax = isUnlimited ? 1 : maxCapacity;
  const progressColor = isUnlimited ? theme.colors.primary700 : 'black';

  return (
    <Card>
      <Flex dir="column" gap="16px">
        <Flex gap="8px" alignItems="center">
          <Icon name="user" size={18} />
          <Text type="Body">참여 현황</Text>
        </Flex>
        <Flex dir="column" gap="8px">
          <Flex justifyContent="space-between">
            <Text type="Body">현재 신청자</Text>
            <Text type="Label">
              {isUnlimited ? '무제한' : `${currentGuestCount} / ${maxCapacity}명`}
            </Text>
          </Flex>
          <ProgressBar value={progressValue} max={progressMax} color={progressColor} />
        </Flex>
      </Flex>
    </Card>
  );
};
