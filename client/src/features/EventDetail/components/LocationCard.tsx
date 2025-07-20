import { Card } from '../../../shared/components/Card';
import { Flex } from '../../../shared/components/Flex';
import { Icon } from '../../../shared/components/Icon';
import { Text } from '../../../shared/components/Text';
import type { EventDetail } from '../types/index';

type LocationCardProps = Pick<EventDetail, 'location'>;

export const LocationCard = ({ location }: LocationCardProps) => (
  <Flex dir="column" css={{ flex: 1, height: '235px' }}>
    <Card css={{ height: '100%' }}>
      <Flex dir="column" gap="16px">
        <Flex gap="8px">
          <Icon name="location" size={18} />
          <Text type="caption">장소</Text>
        </Flex>
        <Text type="caption">{location}</Text>
      </Flex>
    </Card>
  </Flex>
);
