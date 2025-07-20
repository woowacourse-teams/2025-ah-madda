import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Icon } from '../../../shared/components/Icon';
import { ProgressBar } from '../../../shared/components/ProgressBar';
import { Text } from '../../../shared/components/Text';

export const ParticipantsCard = () => (
  <Card>
    <Flex dir="column" gap="16px">
      <Flex gap="8px">
        <Icon name="users" size={18} />
        <Text type="caption">참여 현황</Text>
      </Flex>
      <Flex dir="column" gap="8px">
        <Flex justifyContent="space-between">
          <Text type="caption">현재 신청자</Text>
          <Text type="caption">42 / 50명</Text>
        </Flex>
        <ProgressBar value={42} max={50} color="black" />
      </Flex>
    </Flex>
  </Card>
);
