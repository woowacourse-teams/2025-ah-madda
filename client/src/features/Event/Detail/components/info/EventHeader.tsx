import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate, useParams } from 'react-router-dom';

import { createInviteCode } from '@/api/mutations/useCreateInviteCode';
import { useEventNotificationToggle } from '@/api/mutations/useEventNotificationToggle';
import { InviteCodeModal } from '@/features/Event/Overview/components/InviteCodeModal';
import { Badge } from '@/shared/components/Badge';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { Switch } from '@/shared/components/Switch';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';

import { useModal } from '../../../../../shared/hooks/useModal';
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
            <Text type="Label">{formatDateTime(eventStart, eventEnd)}</Text>
          </Flex>
        </Flex>

        <DesktopRight>
          {isOrganizer ? (
            <>
              <IconButton name="setting" onClick={goManagePage} aria-label="관리" />
              <Flex alignItems="center" gap="8px">
                <Button color="secondary" onClick={handleInviteCodeClick}>
                  공유하기
                </Button>
                <Button color="primary" onClick={goEditPage}>
                  수정
                </Button>
              </Flex>
            </>
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
        </DesktopRight>

        {isOrganizer && (
          <MobileTopRight>
            <Button color="secondary" onClick={handleInviteCodeClick}>
              공유하기
            </Button>
          </MobileTopRight>
        )}
      </Flex>

      {isOrganizer && (
        <MobileFixedCTA>
          <Button size="md" iconName="edit" variant="outline" onClick={goEditPage}>
            수정
          </Button>
          <Button size="md" iconName="setting" onClick={goManagePage}>
            관리
          </Button>
        </MobileFixedCTA>
      )}

      <InviteCodeModal inviteCode={inviteCode} isOpen={isOpen} onClose={close} />
    </>
  );
};

const DesktopRight = styled(Flex)`
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
  min-width: fit-content;

  @media (max-width: 768px) {
    display: none;
  }
`;

const MobileTopRight = styled.div`
  display: none;

  @media (max-width: 768px) {
    display: block;
    min-width: fit-content;
  }
`;

const MobileFixedCTA = styled.div`
  display: none;

  @media (max-width: 768px) {
    display: flex;
    position: fixed;
    bottom: 20px;
    left: 0;
    right: 0;
    z-index: 1000;
    padding: 0 20px;
    gap: 12px;

    > button {
      flex: 1;
    }
  }
`;
