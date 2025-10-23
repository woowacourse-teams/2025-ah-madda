import { useEffect } from 'react';

import { css } from '@emotion/react';
import { useQuery, useSuspenseQueries } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { eventQueryOptions } from '@/api/queries/event';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Flex } from '@/shared/components/Flex';
import { PageLayout } from '@/shared/components/PageLayout';
import { SEO } from '@/shared/components/SEO';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';

import { AttendanceOverview } from '../components/guest/AttendanceOverview';
import { EventBody } from '../components/info/EventBody';
import { EventHeader } from '../components/info/EventHeader';
import { EventDetailContainer } from '../containers/EventDetailContainer';
import { useEventIntroSummaryFocus } from '../hooks/useEventIntroSummaryFocus';

export const EventDetailPage = () => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const { eventId, organizationId } = useParams();

  const [{ data: event }] = useSuspenseQueries({
    queries: [eventQueryOptions.detail(Number(eventId))],
  });

  const { data: organizerStatus } = useQuery({
    ...eventQueryOptions.organizer(Number(eventId)),
    enabled: isAuthenticated(),
  });

  const { data: joinedStatus } = useQuery({
    ...organizationQueryOptions.joinedStatus(Number(organizationId)),
    enabled: !!organizationId && isAuthenticated(),
  });

  const { data: guestStatus } = useQuery({
    ...eventQueryOptions.guestStatus(Number(eventId)),
    enabled: isAuthenticated() && joinedStatus?.isMember,
  });

  const summary = useEventIntroSummaryFocus({
    event,
    isGuest: guestStatus?.isGuest,
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
    <PageLayout>
      <SEO
        title={event.title}
        description={event?.description || ''}
        eventId={Number(eventId)}
        organizationId={Number(organizationId)}
      />
      <EventDetailContainer introDesc={summary}>
        <EventHeader
          isMember={joinedStatus?.isMember || false}
          eventId={Number(eventId)}
          title={event.title}
          place={event.place}
          eventStart={event.eventStart}
          eventEnd={event.eventEnd}
          registrationEnd={event.registrationEnd}
        />
        <Tabs defaultValue="detail">
          <Tabs.List
            aria-label="이벤트 상세 정보와 참여 현황 탭"
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

          <Tabs.Content value="detail" role="region" aria-label="이벤트 정보">
            <EventBody
              organizationId={Number(organizationId)}
              isMember={joinedStatus?.isMember || false}
              isOrganizer={organizerStatus?.isOrganizer || false}
              isGuest={guestStatus?.isGuest || false}
              {...event}
            />
          </Tabs.Content>

          <Tabs.Content value="participation" role="region" aria-label="참여 현황">
            <AttendanceOverview eventId={Number(eventId)} />
          </Tabs.Content>
        </Tabs>
      </EventDetailContainer>
    </PageLayout>
  );
};
