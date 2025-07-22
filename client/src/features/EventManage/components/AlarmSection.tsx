import { useState } from 'react';

import { css } from '@emotion/react';

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

  const handleSendAlarm = () => {
    if (alarmMessage.trim()) {
      // TODO: 알람 전송 로직 구현
      console.log('알람 전송:', alarmMessage);
      setAlarmMessage('');
    }
  };

  return (
    <Card>
      <Flex as="section" dir="column">
        <Flex alignItems="flex-end">
          <Icon name="alarm" size={24} color="#F54900" />
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
            disabled={!alarmMessage.trim()}
            onClick={handleSendAlarm}
            css={css`
              border-radius: 6.75px;
              opacity: ${alarmMessage.trim() ? 1 : 0.5};
              cursor: ${alarmMessage.trim() ? 'pointer' : 'not-allowed'};
            `}
          >
            보내기
          </Button>

          <Text type="caption" weight="regular" color="#6A7282">
            {`${pendingGuestsCount}명의 미신청자에게 알람이 전송됩니다.`}
          </Text>
        </Flex>
      </Flex>
    </Card>
  );
};
