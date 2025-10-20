import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useParams } from 'react-router-dom';

import { createInviteCode } from '@/api/mutations/useCreateInviteCode';
import { useEventNotificationToggle } from '@/api/mutations/useEventNotificationToggle';
import { InviteCodeModal } from '@/features/Event/Overview/components/InviteCodeModal';
import { Badge } from '@/shared/components/Badge';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Switch } from '@/shared/components/Switch';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';
import { formatDate } from '@/shared/utils/dateUtils';

import type { EventDetail } from '../../../types/Event';
import { badgeText } from '../../../utils/badgeText';

type EventHeaderProps = { eventId: number } & Pick<
  EventDetail,
  'title' | 'place' | 'eventStart' | 'eventEnd' | 'registrationEnd'
>;

export const EventHeader = ({
  eventId,
  title,
  place,
  eventStart,
  eventEnd,
  registrationEnd,
}: EventHeaderProps) => {
  const { organizationId } = useParams();
  const status = badgeText(registrationEnd);
  const { isOpen, open: openInviteCodeModal, close } = useModal();
  const { optOut, optIn, isLoading, data } = useEventNotificationToggle(eventId);
  const { error } = useToast();

  const [inviteCode, setInviteCode] = useState('');
  const checked = !data.optedOut;

  const handleInviteCodeClick = async () => {
    const data = await createInviteCode(Number(organizationId));
    const baseUrl =
      process.env.NODE_ENV === 'production'
        ? `https://ahmadda.com/${organizationId}/event/${eventId}`
        : `http://localhost:5173/${organizationId}/event/${eventId}`;
    const inviteUrl = `${baseUrl}/invite?code=${data.inviteCode}`;
    setInviteCode(inviteUrl);
    openInviteCodeModal();
  };

  const handleSwitch = (next: boolean) => {
    if (next === checked) return;

    (next ? optIn : optOut).mutate(undefined, {
      onError: () => {
        error(next ? '알림을 켜는 데 문제가 생겼어요.' : '알림을 끄는 데 문제가 생겼어요.');
      },
    });
  };

  return (
    <>
      <Flex width="100%" justifyContent="space-between" alignItems="flex-start" gap="16px">
        <Flex
          dir="column"
          gap="8px"
          css={css`
            flex: 1;
            min-width: 0;
          `}
        >
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
            <Text type="Label">
              {formatDate({
                start: eventStart,
                end: eventEnd,
                pattern: 'MM.DD E HH:mm',
                options: {
                  dayOfWeek: 'shortParen',
                  smartRange: true,
                },
              })}
            </Text>
          </Flex>
        </Flex>

        <Flex alignItems="center" gap="8px">
          <Text type="Body">알림 받기</Text>
          <Switch
            aria-label="이벤트 알림 수신 설정"
            checked={checked}
            onCheckedChange={handleSwitch}
            disabled={isLoading}
          />
        </Flex>
      </Flex>

      <InviteCodeModal inviteCode={inviteCode} isOpen={isOpen} onClose={close} />
    </>
  );
};
