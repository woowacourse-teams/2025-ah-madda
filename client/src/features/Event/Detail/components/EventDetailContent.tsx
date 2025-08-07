import { useState } from 'react';

import { css } from '@emotion/react';

import { GuestStatusAPIResponse, OrganizerStatusAPIResponse } from '@/api/types/event';
import { Answer } from '@/api/types/event';
import { Flex } from '@/shared/components/Flex';

import { EventDetail } from '../../types/Event';
import { formatKoreanDateTime } from '../utils/formatKoreanDateTime';

import { DescriptionCard } from './DescriptionCard';
import { LocationCard } from './LocationCard';
import { ParticipantsCard } from './ParticipantsCard';
import { PreQuestionCard } from './PreQuestionCard';
import { SubmitButtonCard } from './SubmitButtonCard';
import { TimeInfoCard } from './TimeInfoCard';

type EventDetailContentProps = EventDetail & GuestStatusAPIResponse & OrganizerStatusAPIResponse;

export const EventDetailContent = ({
  eventId,
  registrationEnd,
  eventStart,
  eventEnd,
  place,
  currentGuestCount,
  maxCapacity,
  description,
  questions,
  isGuest,
  isOrganizer,
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
    <Flex dir="column" width="100%" padding="20px 0" gap="20px">
      <Flex
        dir="row"
        gap="24px"
        width="100%"
        css={css`
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
        {place && <LocationCard place={place} />}
      </Flex>

      <ParticipantsCard currentGuestCount={currentGuestCount} maxCapacity={maxCapacity} />
      {description && <DescriptionCard description={description} />}

      {questions.length > 0 && (
        <PreQuestionCard
          questions={questions}
          answers={answers}
          onChangeAnswer={handleChangeAnswer}
        />
      )}
      {!isOrganizer && (
        <SubmitButtonCard
          isGuest={isGuest}
          registrationEnd={registrationEnd}
          eventId={eventId}
          answers={answers}
        />
      )}
    </Flex>
  );
};
