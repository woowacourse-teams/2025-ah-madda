import { useState } from 'react';

import { useNavigate } from 'react-router-dom';

import { useEventNotificationToggle } from '@/api/mutations/useEventNotificationToggle';
import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Switch } from '@/shared/components/Switch';
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
  const status = badgeText(registrationEnd);

  const [receiveNotification, setReceiveNotification] = useState(true);
  const { optOut, undoOptOut, isLoading } = useEventNotificationToggle(eventId);

  const handleSwitch = (next: boolean) => {
    if (next === receiveNotification) return;

    setReceiveNotification(next);

    if (!next) {
      optOut.mutate(undefined, {
        onSuccess: () => {
          alert('이벤트 알림 수신을 거부했습니다.');
        },
        onError: () => {
          setReceiveNotification(true);
          alert('알림 수신 거부 설정에 실패했습니다.');
        },
      });
    } else {
      undoOptOut.mutate(undefined, {
        onSuccess: () => {
          alert('이벤트 알림 수신을 다시 받습니다.');
        },
        onError: () => {
          setReceiveNotification(false);
          alert('알림 수신 설정에 실패했습니다.');
        },
      });
    }
  };

  return (
    <Flex width="100%" justifyContent="space-between" alignItems="center">
      <Flex dir="column" gap="8px">
        <Badge variant={status.color}>{status.text}</Badge>
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
      {isOrganizer ? (
        <Button
          color="secondary"
          variant="outline"
          onClick={() => navigate(`/event/edit/${eventId}`)}
        >
          수정
        </Button>
      ) : (
        <Flex alignItems="center" gap="8px">
          <Text type="Body">알림 받기</Text>
          <Switch
            aria-label="이벤트 알림 수신 설정"
            checked={receiveNotification}
            onCheckedChange={handleSwitch}
            disabled={isLoading}
          />
        </Flex>
      )}
    </Flex>
  );
};
