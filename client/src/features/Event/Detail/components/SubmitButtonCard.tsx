import { Button } from '../../../../shared/components/Button';
import { Card } from '../../../../shared/components/Card';
import { EventDetail } from '../../types/Event';

export const SubmitButtonCard = ({ registrationEnd }: Pick<EventDetail, 'registrationEnd'>) => {
  const now = new Date();
  const isBeforeDeadline = now <= new Date(registrationEnd);

  return (
    <Card>
      <Button
        width="100%"
        color={isBeforeDeadline ? '#2563EB' : 'gray'}
        disabled={!isBeforeDeadline}
      >
        {isBeforeDeadline ? '참가 신청하기' : '신청 마감'}
      </Button>
    </Card>
  );
};
