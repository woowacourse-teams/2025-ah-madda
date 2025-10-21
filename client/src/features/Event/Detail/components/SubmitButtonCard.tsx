import { useCallback, useRef } from 'react';

import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';

import { isAuthenticated } from '@/api/auth';
import { useCancelParticipation } from '@/api/mutations/useCancelParticipation';
import { useParticipateEvent } from '@/api/mutations/useParticipateEvent';
import { Answer, GuestStatusAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';
import { announce } from '@/shared/utils/announce';

import { VerificationModal } from '../../components/VerificationModal';
import { getEventButtonState } from '../utils/getSubmitButtonState';

type SubmitBUttonCardProps = {
  eventId: number;
  registrationEnd: string;
  answers: Answer[];
  onResetAnswers: VoidFunction;
  isRequiredAnswerComplete: boolean;
  isMember: boolean;
} & GuestStatusAPIResponse;

const getErrorMessage = (err: unknown, fallback = '오류가 발생했어요.') => {
  if (err instanceof Error && err.message) return err.message;
  if (typeof err === 'string' && err.trim()) return err;
  try {
    return JSON.stringify(err);
  } catch {
    return fallback;
  }
};

export const SubmitButtonCard = ({
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

  const buttonState = getEventButtonState({
    registrationEnd,
    isGuest,
    isRequiredAnswerComplete,
  });

  const { isOpen, open, close } = useModal();

  const handleParticipantClick = () => {
    if (!isAuthenticated() || !isMember) {
      open();
      announce('로그인이 필요합니다. 로그인 모달이 열렸습니다.');
      return;
    }

    participantMutate(answers, {
      onSuccess: () => {
        onResetAnswers();
        success('✅ 참가 신청이 완료되었습니다.');
        announce('참가 신청이 완료되었습니다. 버튼 재클릭 시 참가 신청을 취소할 수 있습니다.');
        const btn = document.getElementById('event-submit-button') as HTMLButtonElement | null;
        btn?.focus();
      },
      onError: (err: unknown) => {
        const msg = getErrorMessage(err, '신청에 실패했어요.');
        error(`❌ ${msg}`);
        announce(`신청에 실패했습니다. ${msg}`);
      },
    });
  };

  const handleCancelParticipateClick = () => {
    if (!isAuthenticated() || !isMember) {
      open();
      return;
    }

    cancelParticipateMutate(undefined, {
      onSuccess: () => {
        success('✅ 참가 신청이 취소되었습니다.');
        announce('참가 신청이 취소되었습니다. 버튼 재클릭 시 참가 신청할 수 있습니다.');
        const btn = document.getElementById('event-submit-button') as HTMLButtonElement | null;
        btn?.focus();
      },
      onError: (err: unknown) => {
        const msg = getErrorMessage(err, '취소에 실패했어요.');
        error(`❌ ${msg}`);
        announce(`참가 취소에 실패했습니다. ${msg}`);
      },
    });
  };

  const onKeyDown: React.KeyboardEventHandler<HTMLButtonElement> = (e) => {
    if (e.key === 'Enter') {
      e.preventDefault();
      if (buttonState.action === 'cancel') handleCancelParticipateClick();
      else if (buttonState.action === 'participate') handleParticipantClick();
    }
  };

  const ariaLabel = buttonState.action === 'cancel' ? '신청 취소 버튼' : '신청 하기 버튼';

  const btnRef = useRef<HTMLButtonElement>(null);
  const setDescribedByInOrder = useCallback(() => {
    const btn = btnRef.current;
    if (!btn) return;

    const ids: string[] = [];
    if (document.getElementById('event-intro-desc')) ids.push('event-intro-desc');
    if (document.getElementById('event-submit-hint')) ids.push('event-submit-hint');

    if (ids.length) btn.setAttribute('aria-describedby', ids.join(' '));
    else btn.removeAttribute('aria-describedby');
  }, []);

  const onFocus: React.FocusEventHandler<HTMLButtonElement> = () => {
    setDescribedByInOrder();
  };

  return (
    <>
      <Flex
        as="section"
        id="event-submit-section"
        role="region"
        aria-label="신청 섹션"
        tabIndex={-1}
        margin="10px 0 40px"
        dir="column"
        gap="8px"
      >
        <Button
          ref={btnRef}
          id="event-submit-button"
          aria-label={ariaLabel}
          aria-live="off"
          size="full"
          color={buttonState.color}
          disabled={buttonState.disabled}
          onFocus={onFocus}
          onKeyDown={onKeyDown}
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
        onSubmit={handleParticipantClick}
        isMember={isMember}
      />
    </>
  );
};
