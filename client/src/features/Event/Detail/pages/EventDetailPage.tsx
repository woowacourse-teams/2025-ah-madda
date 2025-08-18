import { css } from '@emotion/react';
import { useSuspenseQueries } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';

import { AttendanceOverview } from '../components/guest/AttendanceOverview';
import { EventBody } from '../components/info/EventBody';
import { EventHeader } from '../components/info/EventHeader';
import { EventDetailContainer } from '../containers/EventDetailContainer';

export const EventDetailPage = () => {
  const navigate = useNavigate();
  const { eventId } = useParams();
  const [{ data: event }, { data: guestStatus }, { data: organizerStatus }] = useSuspenseQueries({
    queries: [
      eventQueryOptions.detail(Number(eventId)),
      eventQueryOptions.guestStatus(Number(eventId)),
      eventQueryOptions.organizer(Number(eventId)),
    ],
  });

  if (!event) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#666">
          이벤트를 찾을 수 없습니다.
        </Text>
      </Flex>
    );
  }

  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              onClick={() => navigate('/event')}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Button size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <EventDetailContainer>
        <EventHeader
          isOrganizer={organizerStatus.isOrganizer}
          eventId={Number(eventId)}
          title={event.title}
          place={event.place}
          eventStart={event.eventStart}
          eventEnd={event.eventEnd}
          registrationEnd={event.registrationEnd}
        />
        <Tabs defaultValue="detail">
          <Tabs.List
            css={css`
              width: 30%;
              @media (max-width: 768px) {
                width: 100%;
              }
            `}
          >
            <Tabs.Trigger value="detail">이벤트 정보</Tabs.Trigger>
            <Tabs.Trigger value="participation">참여 현황</Tabs.Trigger>
          </Tabs.List>

          <Tabs.Content value="detail">
            <EventBody
              isOrganizer={organizerStatus.isOrganizer}
              isGuest={guestStatus.isGuest}
              {...event}
            />
          </Tabs.Content>

          <Tabs.Content value="participation">
            <AttendanceOverview eventId={Number(eventId)} />
          </Tabs.Content>
        </Tabs>
      </EventDetailContainer>
    </PageLayout>
  );
};
