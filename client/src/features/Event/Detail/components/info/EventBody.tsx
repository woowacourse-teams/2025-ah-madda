import { useState } from 'react';

import { GuestStatusAPIResponse, OrganizerStatusAPIResponse } from '@/api/types/event';
import { Answer } from '@/api/types/event';
import { Flex } from '@/shared/components/Flex';

import { EventDetail } from '../../../types/Event';
import { SubmitButtonCard } from '../SubmitButtonCard';

import { EventDetails } from './EventDetails';
import { PreQuestionSection } from './PreQuestionSection';

type EventBodyProps = EventDetail & GuestStatusAPIResponse & OrganizerStatusAPIResponse;

export const EventBody = ({
  isOrganizer,
  eventId,
  registrationEnd,
  currentGuestCount,
  maxCapacity,
  description,
  questions,
  isGuest,
  organizerName,
}: EventBodyProps) => {
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
    <Flex dir="column" gap="24px" width="100%">
      <EventDetails
        organizerName={organizerName}
        description={description}
        currentGuestCount={currentGuestCount}
        maxCapacity={maxCapacity}
        registrationEnd={registrationEnd}
      />

      {questions.length > 0 && (
        <PreQuestionSection
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
