import { useState } from 'react';

import { css } from '@emotion/react';

import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';

import { GuestList } from './GuestList';
import { GuestModal } from './GuestModal';

export type Guest = {
  name: string;
  status: string;
};

type GuestManageSectionProps = {
  completedGuests: Guest[];
  pendingGuests: Guest[];
};

export const GuestManageSection = ({ completedGuests, pendingGuests }: GuestManageSectionProps) => {
  const { isOpen, open, close } = useModal();
  const [selectedGuest, setSelectedGuest] = useState<Guest | null>(null);
  const [alarmMessage, setAlarmMessage] = useState('');

  const handleGuestClick = (guest: Guest) => {
    setSelectedGuest(guest);
    open();
  };

  const handleCloseModal = () => {
    close();
    setSelectedGuest(null);
  };

  const handleSendAlarm = () => {
    if (alarmMessage.trim()) {
      // TODO: 알람 전송 로직 구현
      console.log('알람 전송:', alarmMessage);
      setAlarmMessage('');
    }
  };

  return (
    <Flex as="section" dir="column" gap="24px" width="100%">
      <Card>
        <Flex dir="column" gap="20px">
          <Flex alignItems="center" gap="8px">
            <Icon name="users" size={14} color="#4A5565" />
            <Text type="Body" weight="regular" color="#4A5565">
              게스트 조회
            </Text>
          </Flex>

          <GuestList
            title={`신청 완료 (${completedGuests.length}명)`}
            titleColor="#00A63E"
            guests={completedGuests}
            variant="completed"
            onGuestClick={handleGuestClick}
          />

          <GuestList
            title={`미신청 (${pendingGuests.length}명)`}
            titleColor="#4A5565"
            guests={pendingGuests}
            variant="pending"
            onGuestClick={handleGuestClick}
          />
        </Flex>
      </Card>

      <Card>
        <Flex dir="column" gap="16px">
          <Flex alignItems="flex-end">
            <Icon name="alarm" size={24} color="#F54900" />
            <Text type="Body" weight="medium" color="#F54900">
              미신청자 알람
            </Text>
          </Flex>

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
            {`${pendingGuests.length}명의 미신청자에게 알람이 전송됩니다.`}
          </Text>
        </Flex>
      </Card>

      <GuestModal isOpen={isOpen} onClose={handleCloseModal} guest={selectedGuest} />
    </Flex>
  );
};
