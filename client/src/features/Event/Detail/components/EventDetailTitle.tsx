import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

import type { EventDetail } from '../../../Event/types/Event';

type EventHeaderProps = Pick<EventDetail, 'title' | 'organizerName'>;

export const EventDetailTitle = ({ title, organizerName }: EventHeaderProps) => {
  return (
    <Flex dir="column" gap="12px">
      <Text type="Title" weight="bold">
        {title}
      </Text>
      <Flex alignItems="center" gap="4px">
        <Icon name="users" size={18} />
        <Text type="Label">{`주최: ${organizerName}`}</Text>
      </Flex>
    </Flex>
  );
};
