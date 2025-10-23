import { useParams } from 'react-router-dom';

import { badgeText } from '@/features/Event/utils/badgeText';
import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { formatDate } from '@/shared/utils/dateUtils';

type EventHeaderProps = {
  eventId: number;
  title: string;
  place: string;
  eventStart: string;
  eventEnd: string;
  registrationEnd: string;
};

export const EventHeader = ({
  eventId,
  title,
  place,
  eventStart,
  eventEnd,
  registrationEnd,
}: EventHeaderProps) => {
  const { success } = useToast();
  const { organizationId } = useParams();
  const badgeTextValue = badgeText(registrationEnd);

  const handleShareClick = () => {
    navigator.clipboard.writeText(`${window.location.origin}/${organizationId}/event/${eventId}`);
    success('공유 링크가 복사되었습니다.');
  };

  return (
    <>
      <Flex dir="column" gap="12px">
        <Flex dir="row" justifyContent="space-between" alignItems="flex-end">
          <Badge variant={badgeTextValue.color}>{badgeTextValue.text}</Badge>
          <Button size="md" color="secondary" variant="solid" onClick={handleShareClick}>
            공유하기
          </Button>
        </Flex>
        <Text as="h1" type="Display" weight="bold">
          {title}
        </Text>

        <Flex dir="column" gap="4px">
          <Flex dir="row" gap="4px" alignItems="center">
            <Icon name="location" size={18} color="gray500" />
            <Text type="Label">{place}</Text>
          </Flex>
          <Flex dir="row" gap="4px" alignItems="center">
            <Icon name="calendar" size={18} color="gray500" />
            <Text type="Label">
              {formatDate({
                start: eventStart,
                end: eventEnd,
                pattern: 'YYYY. MM. DD A HH시',
                options: {
                  dayOfWeek: 'shortParen',
                },
              })}
            </Text>
          </Flex>
        </Flex>
      </Flex>
    </>
  );
};
