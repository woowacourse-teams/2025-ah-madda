import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { PageLayout } from '@/shared/components/PageLayout';
import { Tabs } from '@/shared/components/Tabs';

import { GuestManageSection } from '../components/guest/GuestManageSection';
import { PreAnswersSection } from '../components/guest/PreAnswersSection';
import { EventHeader } from '../components/info/EventHeader';
import { EventInfoSection } from '../components/info/EventInfoSection';
import { EventManageContainer } from '../containers/EventManageContainer';

export const EventManagePage = () => {
  const { eventId: eventIdParam } = useParams();
  const eventId = Number(eventIdParam);

  const { data: event } = useQuery(eventQueryOptions.detail(eventId));
  const { data: statistics = [] } = useQuery(eventQueryOptions.statistic(eventId));

  if (!event) return null;

  const hasPreQuestions = event.questions.length > 0;

  return (
    <PageLayout>
      <EventManageContainer>
        <EventHeader
          eventId={event.eventId}
          title={event.title}
          place={event.place}
          eventStart={event.eventStart}
          eventEnd={event.eventEnd}
          registrationEnd={event.registrationEnd}
        />

        <Tabs defaultValue="detail">
          <Tabs.List
            css={css`
              width: 40%;
              @media (max-width: 768px) {
                width: 100%;
              }
            `}
          >
            <Tabs.Trigger value="detail">이벤트 정보</Tabs.Trigger>
            <Tabs.Trigger value="applications">참여 현황</Tabs.Trigger>
            {hasPreQuestions && <Tabs.Trigger value="preanswers">사전 질문</Tabs.Trigger>}
          </Tabs.List>

          <Tabs.Content value="detail">
            <EventInfoSection event={event} statistics={statistics} />
          </Tabs.Content>

          <Tabs.Content value="applications">
            <GuestManageSection />
          </Tabs.Content>

          {hasPreQuestions && (
            <Tabs.Content value="preanswers">
              <PreAnswersSection eventId={eventId} />
            </Tabs.Content>
          )}
        </Tabs>
      </EventManageContainer>
    </PageLayout>
  );
};
