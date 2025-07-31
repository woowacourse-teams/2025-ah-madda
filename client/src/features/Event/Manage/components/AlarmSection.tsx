import { useState } from 'react';

import { css } from '@emotion/react';
import { useParams } from 'react-router-dom';

import { useAddAlarm } from '@/api/mutations/useAddAlarm';
import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';

type AlarmSectionProps = {
  pendingGuestsCount: number;
};

export const AlarmSection = ({ pendingGuestsCount }: AlarmSectionProps) => {
  const [alarmMessage, setAlarmMessage] = useState('');
  const { eventId: eventIdParam } = useParams();
  const { mutate: postAlarm, isPending } = useAddAlarm({ eventId: Number(eventIdParam) });

  const handleSendAlarm = () => {
    if (!alarmMessage.trim()) {
      return;
    }

    postAlarm(alarmMessage, {
      onSuccess: () => {
        setAlarmMessage('');
      },
    });
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
        <Flex dir="column" gap="20px">
          <Input
            id="alarm-message"
            label=""
            placeholder="알람 메시지를 입력하세요..."
            value={alarmMessage}
            onChange={(e) => setAlarmMessage(e.target.value)}
            css={css`
              & input {
                background-color: #f3f3f5;
                border: none;
                border-radius: 6.75px;
                padding: 8px 12px;
                font-size: 12.3px;
                color: #717182;
                &::placeholder {
                  color: #717182;
                }
              }
            `}
          />

          <Button
            width="100%"
            size="sm"
            color="#F54900"
            disabled={!alarmMessage.trim() || isPending}
            onClick={handleSendAlarm}
            css={css`
              border-radius: 6.75px;
              opacity: ${alarmMessage.trim() && !isPending ? 1 : 0.5};
              cursor: ${alarmMessage.trim() && !isPending ? 'pointer' : 'not-allowed'};
            `}
          >
            {isPending ? '전송 중...' : '보내기'}
          </Button>

          <Text type="Label" weight="regular" color="#6A7282">
            {`${pendingGuestsCount}명의 미신청자에게 알람이 전송됩니다.`}
          </Text>
        </Flex>
      </Flex>
    </Card>
  );
};
