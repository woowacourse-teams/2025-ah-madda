import { css } from '@emotion/react';

import { Flex } from '../../../shared/components/Flex';
import { Header } from '../../../shared/components/Header';
import { IconButton } from '../../../shared/components/IconButton';
import { PageLayout } from '../../../shared/components/PageLayout';
import { Text } from '../../../shared/components/Text';
import { DescriptionCard } from '../components/DescriptionCard';
import { EventDetailTitle } from '../components/EventDetailTitle';
import { LocationCard } from '../components/LocationCard';
import { ParticipantsCard } from '../components/ParticipantsCard';
import { PreQuestionCard } from '../components/PreQuestionCard';
import { SubmitButtonCard } from '../components/SubmitButtonCard';
import { TimeInfoCard } from '../components/TimeInfoCard';
import { useEventDetail } from '../hooks/useEventDetail';

export const EventDetail = () => {
  const { event, loading, error } = useEventDetail('1');

  if (!event) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#666">
          이벤트를 찾을 수 없습니다.
        </Text>
      </Flex>
    );
  }

  if (loading) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#666">
          이벤트 정보를 불러오는 중...
        </Text>
      </Flex>
    );
  }

  if (error) {
    return (
      <Flex dir="column" justifyContent="center" alignItems="center">
        <Text type="Body" weight="regular" color="#ff4444">
          {error}
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
              <IconButton name="back" size={14} />
              <Text type="caption">돌아가기</Text>
            </Flex>
          }
          css={{
            backgroundColor: 'white',
          }}
        />
      }
    >
      <Flex
        dir="column"
        width="100%"
        margin="0 auto"
        padding="28px 14px"
        gap="24px"
        css={css`
          max-width: 784px;
        `}
      >
        <EventDetailTitle title={event.title} author={event.author} />
        <Flex
          dir="row"
          gap="24px"
          width="100%"
          css={css`
            display: flex;

            @media (max-width: 768px) {
              flex-direction: column;
            }
          `}
        >
          <TimeInfoCard
            deadlineTime={event.deadlineTime}
            startTime={event.startTime}
            endTime={event.endTime}
          />
          <LocationCard location={event.location} />
        </Flex>

        <ParticipantsCard
          currentParticipants={event.currentParticipants}
          maxParticipants={event.maxParticipants}
        />
        <DescriptionCard description={event.description} />
        <PreQuestionCard preQuestions={event.preQuestions} />
        <SubmitButtonCard />
      </Flex>
    </PageLayout>
  );
};
