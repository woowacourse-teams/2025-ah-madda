import { useQuery } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { eventQueryOptions } from '@/api/queries/event';

import { Flex } from '../../../../shared/components/Flex';
import { Header } from '../../../../shared/components/Header';
import { IconButton } from '../../../../shared/components/IconButton';
import { PageLayout } from '../../../../shared/components/PageLayout';
import { Text } from '../../../../shared/components/Text';
import { EventDetailContent } from '../components/EventDetailContent';

export const EventDetailPage = () => {
  const navigate = useNavigate();
  const { eventId } = useParams();
  const { data: event } = useQuery(eventQueryOptions.detail(Number(eventId)));

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
            <Flex alignItems="center" gap="12px">
              <IconButton name="back" size={14} onClick={() => navigate(-1)} />
              <Text type="caption">돌아가기</Text>
            </Flex>
          }
        />
      }
    >
      <EventDetailContent event={event} />
    </PageLayout>
  );
};
