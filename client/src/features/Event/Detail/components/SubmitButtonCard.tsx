import { css } from '@emotion/react';

import { useCancelParticipation } from '@/api/mutations/useCancelParticipation';
import { useParticipateEvent } from '@/api/mutations/useParticipateEvent';
import { Answer, GuestStatusAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { useToast } from '@/shared/components/Toast/ToastContext';

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

  const { success, error } = useToast();
  const { mutate: participantMutate } = useParticipateEvent(eventId);
  const { mutate: cancelParticipateMutate } = useCancelParticipation(eventId);
  
  const buttonState = getEventButtonState({
    registrationEnd,
    isGuest,
    isRequiredAnswerComplete,
  });

  const handleParticipantClick = () => {
    participantMutate(answers, {
      onSuccess: () => {
        onResetAnswers();
        success('✅ 참가 신청이 완료되었습니다.');
      },
      onError: () => {
        error('❌ 신청에 실패했어요.');
      },
    });
  };

  const handleCancelParticipateClick = () => {
    cancelParticipateMutate(undefined, {
      onSuccess: () => {
        success('✅ 참가 신청이 취소되었습니다.');
      },
      onError: (err) => {
        error(`${err.message}`);
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
