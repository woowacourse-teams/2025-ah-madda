import { useMutation } from '@tanstack/react-query';

import { eventQueryOptions } from '../../../../api/queries/event';
import { Button } from '../../../../shared/components/Button';
import { Card } from '../../../../shared/components/Card';

import { Answer } from './EventDetailContent';

type SubmitBUttonCardProps = {
  eventId: number;
  registrationEnd: string;
  answers: Answer[];
};

export const SubmitButtonCard = ({ eventId, registrationEnd, answers }: SubmitBUttonCardProps) => {
  const now = new Date();
  const isBeforeDeadline = now <= new Date(registrationEnd);

  const { mutate, isPending } = useMutation<void, unknown, Answer[]>({
    ...eventQueryOptions.participate(eventId),
  });

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
    <Card>
      <Button
        width="100%"
        color={isBeforeDeadline ? '#2563EB' : 'gray'}
        disabled={!isBeforeDeadline || isPending}
        onClick={handleClick}
      >
        {isBeforeDeadline ? (isPending ? '신청 중...' : '참가 신청하기') : '신청 마감'}
      </Button>
    </Card>
  );
};
