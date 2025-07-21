import { useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
import { Input } from '@/shared/components/Input';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';

import { GuestModal } from './GuestModal';

type Guest = {
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
        <Flex dir="column" gap="20px" padding="24px">
          <Flex alignItems="center" gap="8px">
            <Icon name="users" size={14} color="#4A5565" />
            <Text type="Body" weight="regular" color="#4A5565">
              게스트 조회
            </Text>
          </Flex>

          <Flex dir="column" gap="16px">
            <Text type="Body" weight="medium" color="#00A63E">
              {`신청 완료 (${completedGuests.length}명)`}
            </Text>

            <Flex dir="column" gap="12px">
              {completedGuests.map((guest, index) => (
                <Flex
                  key={index}
                  justifyContent="space-between"
                  alignItems="center"
                  onClick={() => handleGuestClick(guest)}
                  padding="12px 16px"
                  css={{
                    backgroundColor: '#F0FDF4',
                    borderRadius: '8px',
                    cursor: 'pointer',
                    '&:hover': {
                      backgroundColor: '#E6F2E6',
                    },
                  }}
                >
                  <Text type="Body" weight="regular" color="#0A0A0A">
                    {guest.name}
                  </Text>
                  <Flex
                    alignItems="center"
                    gap="8px"
                    padding="3.75px 7.8px 4.75px 8px"
                    justifyContent="center"
                    css={{
                      borderRadius: '6.75px',
                      background: '#DCFCE7',
                    }}
                  >
                    <Text type="caption" weight="regular" color="#4CAF50">
                      {guest.status}
                    </Text>
                  </Flex>
                </Flex>
              ))}
            </Flex>
          </Flex>

          <Flex dir="column" gap="16px">
            <Text type="caption" weight="regular" color="#4A5565">
              {`미신청 (${pendingGuests.length}명)`}
            </Text>

            <Flex dir="column" gap="12px">
              {pendingGuests.map((guest, index) => (
                <Flex
                  key={index}
                  justifyContent="space-between"
                  alignItems="center"
                  padding="12px 16px"
                  onClick={() => handleGuestClick(guest)}
                  css={{
                    backgroundColor: '#F9FAFB',
                    borderRadius: '8px',
                    cursor: 'pointer',
                    '&:hover': {
                      backgroundColor: '#1414140d',
                    },
                  }}
                >
                  <Text type="Body" weight="regular" color="#0A0A0A">
                    {guest.name}
                  </Text>
                  <Flex
                    alignItems="center"
                    gap="8px"
                    padding="3.75px 7.8px 4.75px 8px"
                    justifyContent="center"
                    css={{
                      borderRadius: '6.75px',
                      background: '#ECEEF2',
                    }}
                  >
                    <Text type="caption" weight="regular" color="#666">
                      {guest.status}
                    </Text>
                  </Flex>
                </Flex>
              ))}
            </Flex>
          </Flex>
        </Flex>
      </Card>

      <Card>
        <Flex dir="column" gap="16px" padding="24px">
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
            css={{
              '& input': {
                backgroundColor: '#F3F3F5',
                border: 'none',
                borderRadius: '6.75px',
                padding: '8px 12px',
                fontSize: '12.3px',
                color: '#717182',
                '&::placeholder': {
                  color: '#717182',
                },
              },
            }}
          />

          <Button
            width="100%"
            size="sm"
            color="#F54900"
            disabled={!alarmMessage.trim()}
            onClick={handleSendAlarm}
            css={{
              borderRadius: '6.75px',
              opacity: alarmMessage.trim() ? 1 : 0.5,
              cursor: alarmMessage.trim() ? 'pointer' : 'not-allowed',
            }}
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
