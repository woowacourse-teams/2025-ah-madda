import { css } from '@emotion/react';
import { useNavigate, useParams } from 'react-router-dom';

import { useEventNotificationToggle } from '@/api/mutations/useEventNotificationToggle';
import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { Switch } from '@/shared/components/Switch';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';

import type { EventDetail } from '../../../types/Event';
import { badgeText } from '../../../utils/badgeText';
import { formatDateTime } from '../../../utils/formatDateTime';

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
  const { organizationId } = useParams();
  const status = badgeText(registrationEnd);

  const { optOut, optIn, isLoading, data } = useEventNotificationToggle(eventId);
  const { error } = useToast();

  const checked = !data.optedOut;

  const handleSwitch = (next: boolean) => {
    if (next === checked) return;

    (next ? optIn : optOut).mutate(undefined, {
      onError: () => {
        error(next ? '알림을 켜는 데 문제가 생겼어요.' : '알림을 끄는 데 문제가 생겼어요.');
      },
    });
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
        <Flex gap="8px">
          <Button
            color="secondary"
            variant="outline"
            onClick={() => navigate(`/${organizationId}/event/edit/${eventId}`)}
            css={css`
              @media (max-width: 480px) {
                display: none;
              }
            `}
          >
            수정
          </Button>
          <Button
            color="secondary"
            variant="outline"
            onClick={() => navigate(`/${organizationId}/event/manage/${eventId}`)}
            css={css`
              @media (max-width: 480px) {
                display: none;
              }
            `}
          >
            관리
          </Button>

          <IconButton
            name="pencil"
            aria-label="이벤트 수정 페이지로 이동"
            onClick={() => navigate(`/${organizationId}/event/edit/${eventId}`)}
            css={css`
              display: none;
              @media (max-width: 480px) {
                display: inline-flex;
              }
            `}
          />
          <IconButton
            name="setting"
            aria-label="관리 페이지로 이동"
            onClick={() => navigate(`/${organizationId}/event/manage/${eventId}`)}
            css={css`
              display: none;
              @media (max-width: 480px) {
                display: inline-flex;
              }
            `}
          />
        </Flex>
      ) : (
        <Flex alignItems="center" gap="8px">
          <Text type="Body">알림 받기</Text>
          <Switch
            aria-label="이벤트 알림 수신 설정"
            checked={checked}
            onCheckedChange={handleSwitch}
            disabled={isLoading}
          />
        </Flex>
      )}
    </Flex>
  );
};
