import { useState } from 'react';

import { Card } from '@/shared/components/Card';
import { Flex } from '@/shared/components/Flex';
import { Icon } from '@/shared/components/Icon';
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

  const handleGuestClick = (guest: Guest) => {
    setSelectedGuest(guest);
    open();
  };

  const handleCloseModal = () => {
    close();
    setSelectedGuest(null);
  };

  return (
    <Flex as="section" dir="column" gap="24px" css={{ width: '100%' }}>
      <Card>
        <Flex dir="column" gap="20px" css={{ padding: '24px' }}>
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
                  css={{
                    padding: '12px 16px',
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
                    css={{
                      padding: '3.75px 7.8px 4.75px 8px',
                      justifyContent: 'center',
                      alignItems: 'center',
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
                  onClick={() => handleGuestClick(guest)}
                  css={{
                    padding: '12px 16px',
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
                    css={{
                      padding: '3.75px 7.8px 4.75px 8px',
                      justifyContent: 'center',
                      alignItems: 'center',
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

      <GuestModal isOpen={isOpen} onClose={handleCloseModal} guest={selectedGuest} />
    </Flex>
  );
};
