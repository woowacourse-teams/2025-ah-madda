import { useState } from 'react';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Input } from '@/shared/components/Input';
import { Modal } from '@/shared/components/Modal';
import { Text } from '@/shared/components/Text';

type Guest = {
  name: string;
  status: string;
};

type GuestModalProps = {
  isOpen: boolean;
  onClose: () => void;
  guest: Guest | null;
};

export const GuestModal = ({ isOpen, onClose, guest }: GuestModalProps) => {
  const [memo, setMemo] = useState('');

  const handleSave = () => {
    // TODO: 메모 저장 로직 구현
    setMemo('');
    onClose();
  };

  const handleClose = () => {
    setMemo('');
    onClose();
  };

  if (!guest) return null;

  return (
    <Modal isOpen={isOpen} onClose={handleClose} size="md">
      <Flex dir="column" padding="16px">
        <Flex dir="row" gap="12px">
          <Text type="Body" weight="semibold" color="#0A0A0A">
            {guest.name}
          </Text>
          <Flex
            padding="3.75px 7.8px 4.75px 8px"
            css={{
              borderRadius: '6.75px',
              background: guest.status === '신청 완료' ? '#DCFCE7' : '#ECEEF2',
            }}
          >
            <Text
              type="caption"
              weight="semibold"
              color={guest.status === '신청 완료' ? '#4CAF50' : '#666'}
            >
              {guest.status}
            </Text>
          </Flex>
        </Flex>

        <Flex dir="column" gap="12px">
          <Input
            id="guest-memo"
            label="메모"
            placeholder="게스트에 대한 메모를 입력하세요..."
            value={memo}
            onChange={(e) => setMemo(e.target.value)}
            css={{
              height: '56px',
              borderRadius: '8px',
              padding: '12px 16px',
              backgroundColor: '#8b8c8c26',
            }}
          />

          <Flex gap="12px" justifyContent="flex-end">
            <Button
              variant="outlined"
              color="#666"
              css={{
                width: '65px',
                height: '32px',
                borderRadius: '4px',
                outline: 'none',
                border: '1px solid #E5E7EB',
                '&:focus': {
                  border: '1px solid #0A0A0A',
                },
              }}
              onClick={handleClose}
            >
              <Text type="caption" weight="regular" color="#666">
                취소
              </Text>
            </Button>
            <Button
              variant="filled"
              color="#0A0A0A"
              css={{
                width: '65px',
                height: '32px',
                borderRadius: '4px',
                outline: 'none',
                border: '1px solid #E5E7EB',
                '&:focus': {
                  border: '1px solid #0A0A0A',
                },
              }}
              onClick={handleSave}
            >
              <Text type="caption" weight="regular" color="#FFF">
                저장
              </Text>
            </Button>
          </Flex>
        </Flex>
      </Flex>
    </Modal>
  );
};
