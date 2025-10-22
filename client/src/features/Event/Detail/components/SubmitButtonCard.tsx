import { css } from '@emotion/react';

import { isAuthenticated } from '@/api/auth';
import { useCancelParticipation } from '@/api/mutations/useCancelParticipation';
import { useParticipateEvent } from '@/api/mutations/useParticipateEvent';
import { useParticipateOrganization } from '@/api/mutations/useParticipateOrganization';
import { Answer, GuestStatusAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';

import { VerificationModal } from '../../components/VerificationModal';
import { getEventButtonState } from '../utils/getSubmitButtonState';

type SubmitBUttonCardProps = {
  organizationId: number;
  eventId: number;
  registrationEnd: string;
  answers: Answer[];
  onResetAnswers: VoidFunction;
  isRequiredAnswerComplete: boolean;
  isMember: boolean;
} & GuestStatusAPIResponse;

export const SubmitButtonCard = ({
  organizationId,
  eventId,
  answers,
  registrationEnd,
  isGuest,
  isMember,
  onResetAnswers,
  isRequiredAnswerComplete,
}: SubmitBUttonCardProps) => {
  const { success, error } = useToast();
  const { mutate: participantMutate } = useParticipateEvent(eventId);
  const { mutate: cancelParticipateMutate } = useCancelParticipation(eventId);
  const { mutate: joinOrganization } = useParticipateOrganization(organizationId);
  const buttonState = getEventButtonState({
    registrationEnd,
    isGuest,
    isRequiredAnswerComplete,
  });

  const { isOpen, open, close } = useModal();

  const handleParticipantClick = () => {
    if (!isAuthenticated() || !isMember) {
      open();
      return;
    }

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

  const handleJoinOrganization = (inviteCode: string) => {
    joinOrganization(
      { inviteCode },
      {
        onError: (err) => {
          error(err.message);
        },
      }
    );
  };

  const handleCancelParticipateClick = () => {
    if (!isAuthenticated() || !isMember) {
      open();
      return;
    }

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
    <>
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
      <VerificationModal
        isOpen={isOpen}
        onClose={close}
        onJoinOrganization={handleJoinOrganization}
        onSubmit={handleParticipantClick}
        isMember={isMember}
      />
    </>
  );
};
