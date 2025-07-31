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

  const { mutate } = useParticipateEvent(eventId);

  const handleClick = () => {
    mutate(answers, {
      onSuccess: () => {
        alert('✅ 참가 신청이 완료되었습니다.');
      },
      onError: () => {
        alert('❌ 신청에 실패했어요.');
      },
    });
  };

  return (
    <Flex margin="10px 0 40px">
      <Button
        size="full"
        color={!isGuest || isBeforeDeadline ? 'primary' : 'tertiary'}
        disabled={!isBeforeDeadline || isGuest}
        onClick={handleClick}
      >
        {isBeforeDeadline ? (!isGuest ? '신청 하기' : '신청 완료') : '신청 마감'}
      </Button>
    </Flex>
  );
};
