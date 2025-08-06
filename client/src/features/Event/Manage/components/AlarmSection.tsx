import { useParams } from 'react-router-dom';

import { useAddAlarm } from '@/api/mutations/useAddAlarm';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Input } from '@/shared/components/Input';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';

import { useNotificationForm } from '../hooks/useNotificationForm';

export type AlarmSectionProps = {
  organizationMemberIds: number[];
  selectedGuestCount: number;
};

export const AlarmSection = ({ organizationMemberIds, selectedGuestCount }: AlarmSectionProps) => {
  const { content, handleContentChange, resetContent } = useNotificationForm();
  const { eventId: eventIdParam } = useParams();
  const { mutate: postAlarm, isPending } = useAddAlarm({ eventId: Number(eventIdParam) });

  const handleSendAlarm = () => {
    postAlarm(
      { organizationMemberIds, content },
      {
        onSuccess: () => {
          resetContent();
          return alert('알람이 성공적으로 전송되었습니다.');
        },
        onError: (error) => {
          return alert(`알람 전송에 실패했습니다. ${error.message}`);
        },
      }
    );
  };

  return (
    <Card>
      <Flex as="section" dir="column">
        <Flex alignItems="center" gap="8px">
          <Icon name="alarm" size={14} color="secondary" />
          <Text type="Body" weight="medium" color="#F54900">
            미신청자 알람
          </Text>
        </Flex>
        <Flex dir="column">
          <Input
            id="alarm-message"
            label=""
            placeholder="알람 메시지를 입력하세요..."
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
          <Spacing height="8px" />
          <Text type="Label" weight="regular" color="#6A7282">
            {`선택한 ${selectedGuestCount}명에게 알람이 전송됩니다.`}
          </Text>
        </Flex>
      </Flex>
    </Card>
  );
};
