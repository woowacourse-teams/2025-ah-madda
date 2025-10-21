import { useParams } from 'react-router-dom';

import { useAddAlarm } from '@/api/mutations/useAddAlarm';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { trackSendAlarm } from '@/shared/lib/gaEvents';

import { useNotificationForm } from '../../hooks/useNotificationForm';

type AlarmComposerProps = {
  organizationMemberIds: number[];
  selectedGuestCount: number;
};

export const AlarmComposer = ({
  organizationMemberIds,
  selectedGuestCount,
}: AlarmComposerProps) => {
  const { eventId: eventIdParam } = useParams();
  const { content, handleContentChange, resetContent } = useNotificationForm();
  const { mutate: postAlarm, isPending } = useAddAlarm({ eventId: Number(eventIdParam) });
  const { success, error } = useToast();

  const handleSendAlarm = () => {
    if (content.length > 20) {
      error('20자 이내로 입력해주세요.');
      return;
    }

    trackSendAlarm(selectedGuestCount);
    postAlarm(
      { organizationMemberIds, content },
      {
        onSuccess: () => {
          resetContent();
          success('알람이 성공적으로 전송되었습니다.');
        },
        onError: (err) => {
          error(err.message);
        },
      }
    );
  };

  return (
    <Flex dir="column" alignItems="center" gap="8px">
      <Input
        showCounter
        id="alarm-message"
        placeholder="알람 메시지를 입력해주세요"
        maxLength={20}
        value={content}
        onChange={handleContentChange}
      />
      <Button
        size="full"
        color="primary"
        disabled={!content || organizationMemberIds.length === 0 || isPending}
        onClick={handleSendAlarm}
      >
        {isPending ? '전송 중...' : '보내기'}
      </Button>
    </Flex>
  );
};
