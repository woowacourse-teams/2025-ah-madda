import { useCancelParticipation } from '@/api/mutations/useCancelParticipation';
import { useParticipateEvent } from '@/api/mutations/useParticipateEvent';
import { Answer, GuestStatusAPIResponse } from '@/api/types/event';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';

type SubmitBUttonCardProps = {
  eventId: number;
  registrationEnd: string;
  answers: Answer[];
} & GuestStatusAPIResponse;

export const SubmitButtonCard = ({
  eventId,
  answers,
  registrationEnd,
  isGuest,
}: SubmitBUttonCardProps) => {
  const now = new Date();
  const isBeforeDeadline = now <= new Date(registrationEnd);

  const { mutate: participantMutate } = useParticipateEvent(eventId);
  const { mutate: cancelParticipateMutate } = useCancelParticipation(eventId);

  const handleParticipantClick = () => {
    participantMutate(answers, {
      onSuccess: () => {
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
        color={!isGuest || isBeforeDeadline ? 'primary' : 'tertiary'}
        disabled={!isBeforeDeadline}
        onClick={isGuest ? handleCancelParticipateClick : handleParticipantClick}
      >
        {isBeforeDeadline
          ? isGuest
            ? '신청 취소'
            : '신청 하기'
          : isGuest
            ? '신청 완료'
            : '신청 마감'}
      </Button>
    </Flex>
  );
};
