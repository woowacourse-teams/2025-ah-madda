import { css } from '@emotion/react';

import { Flex } from '../../../../shared/components/Flex';
import { EventDetail } from '../types';

import { DescriptionCard } from './DescriptionCard';
import { EventDetailTitle } from './EventDetailTitle';
import { LocationCard } from './LocationCard';
import { ParticipantsCard } from './ParticipantsCard';
import { PreQuestionCard } from './PreQuestionCard';
import { SubmitButtonCard } from './SubmitButtonCard';
import { TimeInfoCard } from './TimeInfoCard';

type EventDetailContentProps = {
  event: EventDetail;
};

export const EventDetailContent = ({ event }: EventDetailContentProps) => {
  const {
    title,
    author,
    deadlineTime,
    startTime,
    endTime,
    location,
    currentParticipants,
    maxParticipants,
    description,
    preQuestions,
  } = event;

  return (
    <Flex
      dir="column"
      width="100%"
      margin="0 auto"
      padding="80px 24px 0"
      gap="24px"
      css={css`
        max-width: 784px;
      `}
    >
      <EventDetailTitle title={title} author={author} />
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
        <TimeInfoCard deadlineTime={deadlineTime} startTime={startTime} endTime={endTime} />
        <LocationCard location={location} />
      </Flex>

      <ParticipantsCard
        currentParticipants={currentParticipants}
        maxParticipants={maxParticipants}
      />
      <DescriptionCard description={description} />
      <PreQuestionCard preQuestions={preQuestions} />
      <SubmitButtonCard />
    </Flex>
  );
};
