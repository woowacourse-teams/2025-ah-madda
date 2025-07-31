import { css } from '@emotion/react';
import { useSuspenseQueries } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { Tabs } from '@/shared/components/Tabs';
import { Text } from '@/shared/components/Text';

import { ApplicationsTab } from '../components/ApplicationsTab';
import { EventDetailContent } from '../components/EventDetailContent';

export const EventDetailPage = () => {
  const navigate = useNavigate();
  const { eventId } = useParams();
  const [{ data: event }, { data: guestStatus }] = useSuspenseQueries({
    queries: [
      eventQueryOptions.detail(Number(eventId)),
      eventQueryOptions.guestStatus(Number(eventId)),
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
          left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
          right={
            <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <Tabs defaultValue="detail">
        <Tabs.List
          css={css`
            margin-top: 59px;
          `}
        >
          <Tabs.Trigger value="detail">이벤트 정보</Tabs.Trigger>
          <Tabs.Trigger value="applications">신청 현황</Tabs.Trigger>
        </Tabs.List>

        <Tabs.Content value="detail">
          <EventDetailContent isGuest={guestStatus.isGuest} {...event} />
        </Tabs.Content>

        <Tabs.Content value="applications">
          <ApplicationsTab eventId={Number(eventId)} />
        </Tabs.Content>
      </Tabs>
    </PageLayout>
  );
};
