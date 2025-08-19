import { css } from '@emotion/react';

import { useCancelParticipation } from '@/api/mutations/useCancelParticipation';
import { useParticipateEvent } from '@/api/mutations/useParticipateEvent';
import { Answer, GuestStatusAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';

import { getEventButtonState } from '../utils/getSubmitButtonState';

type SubmitBUttonCardProps = {
  eventId: number;
  registrationEnd: string;
  answers: Answer[];
  onResetAnswers: VoidFunction;
  isRequiredAnswerComplete: boolean;
} & GuestStatusAPIResponse;

export const SubmitButtonCard = ({
  eventId,
  answers,
  registrationEnd,
  isGuest,
  onResetAnswers,
  isRequiredAnswerComplete,
}: SubmitBUttonCardProps) => {
  const buttonState = getEventButtonState({
    registrationEnd,
    isGuest,
    isRequiredAnswerComplete,
  });

  const { mutate: participantMutate } = useParticipateEvent(eventId);
  const { mutate: cancelParticipateMutate } = useCancelParticipation(eventId);

  const handleParticipantClick = () => {
    participantMutate(answers, {
      onSuccess: () => {
        onResetAnswers();
        alert('✅ 참가 신청이 완료되었습니다.');
      },
      onError: () => {
        alert('❌ 신청에 실패했어요.');
      },
    });
  };

  const handleCancelParticipateClick = () => {
    cancelParticipateMutate(undefined, {
      onSuccess: () => {
        alert('✅ 참가 신청이 취소되었습니다.');
      },
      onError: (error) => {
        alert(`${error.message}`);
      },
    });
  };

  return (
    <Flex margin="10px 0 40px">
      <Button
        size="full"
        color={buttonState.color}
        disabled={buttonState.disabled}
        onClick={
          buttonState.action === 'cancel' ? handleCancelParticipateClick : handleParticipantClick
        }
        css={css`
          transition: all 0.2s ease-in-out;
        `}
      >
        {buttonState.text}
      </Button>
    </Flex>
  );
};
