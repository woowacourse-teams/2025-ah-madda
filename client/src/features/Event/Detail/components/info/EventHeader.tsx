import { useState } from 'react';

import { useNavigate, useParams } from 'react-router-dom';

import { createInviteCode } from '@/api/mutations/useCreateInviteCode';
import { useEventNotificationToggle } from '@/api/mutations/useEventNotificationToggle';
import { InviteCodeModal } from '@/features/Event/Overview/components/InviteCodeModal';
import { Badge } from '@/shared/components/Badge';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Switch } from '@/shared/components/Switch';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';

import { useModal } from '../../../../../shared/hooks/useModal';
import type { EventDetail } from '../../../types/Event';
import { badgeText } from '../../../utils/badgeText';
import { formatDateTime } from '../../../utils/formatDateTime';

import { EventActionButton } from './EventActionButton';

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
  const { isOpen, open: openInviteCodeModal, close } = useModal();
  const { optOut, optIn, isLoading, data } = useEventNotificationToggle(eventId);
  const { error } = useToast();

  const [inviteCode, setInviteCode] = useState('');

  const checked = !data.optedOut;

  const goEditPage = () => {
    navigate(`/${organizationId}/event/edit/${eventId}`);
  };

  const goManagePage = () => {
    navigate(`/${organizationId}/event/manage/${eventId}`);
  };

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
          <EventActionButton
            onEditEvent={goEditPage}
            onShareEvent={handleInviteCodeClick}
            onManageEvent={goManagePage}
          />
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
      <InviteCodeModal inviteCode={inviteCode} isOpen={isOpen} onClose={close} />
    </>
  );
};
