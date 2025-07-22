import { Card } from '../../../../shared/components/Card';
import { Flex } from '../../../../shared/components/Flex';
import { Icon } from '../../../../shared/components/Icon';
import { ProgressBar } from '../../../../shared/components/ProgressBar';
import { Text } from '../../../../shared/components/Text';
import type { EventDetail } from '../types/index';

type ParticipantsCardProps = Pick<EventDetail, 'currentParticipants' | 'maxParticipants'>;

export const ParticipantsCard = ({
  currentParticipants,
  maxParticipants,
}: ParticipantsCardProps) => {
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
            <Text type="caption">{`${currentParticipants} / ${maxParticipants}명`}</Text>
          </Flex>
          <ProgressBar value={42} max={50} color="black" />
        </Flex>
      </Flex>
    </Card>
  );
};
