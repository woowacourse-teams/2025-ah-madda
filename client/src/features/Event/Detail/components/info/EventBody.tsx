import { GuestStatusAPIResponse, OrganizerStatusAPIResponse } from '@/api/types/event';
import { EventDetail } from '@/api/types/event';
import { OrganizationJoinedStatusAPIResponse } from '@/api/types/organizations';

import { Flex } from '@/shared/components/Flex';

import { useAnswers } from '../../hooks/useAnswers';
import { SubmitButtonCard } from '../SubmitButtonCard';

import { EventDetails } from './EventDetails';
import { PreQuestionSection } from './PreQuestionSection';

type EventBodyProps = EventDetail &
  GuestStatusAPIResponse &
  OrganizerStatusAPIResponse &
  OrganizationJoinedStatusAPIResponse;

export const EventBody = ({
  isMember,
  isOrganizer,
  eventId,
  registrationEnd,
  currentGuestCount,
  maxCapacity,
  description,
  questions,
  isGuest,
  organizerNicknames,
}: EventBodyProps) => {
  const { answers, handleChangeAnswer, resetAnswers, isRequiredAnswerComplete } =
    useAnswers(questions);

  return (
    <Flex dir="column" gap="24px" width="100%">
      <EventDetails
        organizerNicknames={organizerNicknames}
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
          isMember={isMember}
          eventId={eventId}
          registrationEnd={registrationEnd}
          answers={answers}
          onResetAnswers={resetAnswers}
          isRequiredAnswerComplete={isRequiredAnswerComplete()}
        />
      )}
    </Flex>
  );
};
