import { useNavigate } from 'react-router-dom';

import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';

import { badgeText } from '../../../Overview/utils/badgeText';
import { formatDateTime } from '../../../Overview/utils/formatDateTime';
import type { EventDetail } from '../../../types/Event';

type EventHeaderProps = { eventId: number; isOrganizer: boolean } & Pick<
  EventDetail,
  'title' | 'place' | 'eventStart' | 'eventEnd' | 'registrationEnd'
>;

export const EventHeader = ({
  eventId,
  isOrganizer,
  title,
  place,
  eventStart,
  eventEnd,
  registrationEnd,
}: EventHeaderProps) => {
  const navigate = useNavigate();

  return (
    <Flex width="100%" justifyContent="space-between" alignItems="center">
      <Flex dir="column" gap="8px">
        <Badge
          variant={badgeText(registrationEnd).color}
        >{`${badgeText(registrationEnd).text}`}</Badge>
        <Text as="h1" type="Display" weight="bold">
          {title}
        </Text>
        <Flex alignItems="center" gap="4px">
          <Icon name="location" color="gray500" size={18} />
          <Text type="Label">{place}</Text>
        </Flex>
        <Flex alignItems="center" gap="4px">
          <Icon name="clock" color="gray500" size={18} />
          <Text type="Label">{`${formatDateTime(eventStart, eventEnd)}`}</Text>
        </Flex>
      </Flex>
      {isOrganizer && (
        <Button
          color="secondary"
          variant="outline"
          onClick={() => navigate(`/event/edit/${eventId}`)}
        >
          수정
        </Button>
      )}
    </Flex>
  );
};
