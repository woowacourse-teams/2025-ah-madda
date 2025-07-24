import { useState } from 'react';

import { css } from '@emotion/react';

import { Flex } from '../../../../shared/components/Flex';
import { EventDetail } from '../../../Event/types/Event';
import { formatKoreanDateTime } from '../utils/formatKoreanDateTime';

import { DescriptionCard } from './DescriptionCard';
import { EventDetailTitle } from './EventDetailTitle';
import { LocationCard } from './LocationCard';
import { ParticipantsCard } from './ParticipantsCard';
import { PreQuestionCard } from './PreQuestionCard';
import { SubmitButtonCard } from './SubmitButtonCard';
import { TimeInfoCard } from './TimeInfoCard';

type EventDetailContentProps = EventDetail;

export type Answer = {
  questionId: number;
  answerText: string;
};

export const EventDetailContent = ({
  eventId,
  title,
  organizerName,
  registrationEnd,
  eventStart,
  eventEnd,
  place,
  currentGuestCount,
  maxCapacity,
  description,
  questions,
}: EventDetailContentProps) => {
  const [answers, setAnswers] = useState<Answer[]>(
    questions.map(({ questionId }) => ({
      questionId,
      answerText: '',
    }))
  );

  const handleChangeAnswer = (questionId: number, answerText: string) => {
    setAnswers((prev) =>
      prev.map((answer) => (answer.questionId === questionId ? { ...answer, answerText } : answer))
    );
  };

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
      <EventDetailTitle title={title} organizerName={organizerName} />
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
          registrationEnd={formatKoreanDateTime(registrationEnd)}
          eventStart={formatKoreanDateTime(eventStart)}
          eventEnd={formatKoreanDateTime(eventEnd)}
        />
        <LocationCard place={place} />
      </Flex>

      <ParticipantsCard currentGuestCount={currentGuestCount} maxCapacity={maxCapacity} />
      <DescriptionCard description={description} />
      <PreQuestionCard
        questions={questions}
        answers={answers}
        onChangeAnswer={handleChangeAnswer}
      />

      <SubmitButtonCard registrationEnd={registrationEnd} eventId={eventId} answers={answers} />
    </Flex>
  );
};
