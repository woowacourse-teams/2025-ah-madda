import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { HttpError } from '@/api/fetcher';
import { usePoke } from '@/api/mutations/usePoke';
import { POKE_MESSAGES_TYPE } from '@/api/types/notification';
import { NonGuest } from '@/features/Event/Manage/types';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Modal, ModalProps } from '@/shared/components/Modal/Modal';
import { Text } from '@/shared/components/Text';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { theme } from '@/shared/styles/theme';

type PokeModalProps = {
  eventId: number;
  receiverGuest: NonGuest;
} & ModalProps;

// eslint-disable-next-line react-refresh/only-export-components
export const POKE_MESSAGES = {
  RECOMMEND: '이벤트 참여를 추천했어요! 🌈',
  WAITING: '참여를 기다려요 ⏰',
  ARRIVED: '포키가 도착했어요! ✨',
  HEART: '당신을 포키했어요! ❤️',
};

export const PokeModal = ({ eventId, receiverGuest, isOpen, onClose }: PokeModalProps) => {
  const { success, error } = useToast();
  const { mutate: pokeMutate } = usePoke(eventId);
  const [selectedMessage, setSelectedMessage] = useState<POKE_MESSAGES_TYPE>('RECOMMEND');

  const handleClickMessage = (pokeMessage: POKE_MESSAGES_TYPE) => {
    setSelectedMessage(pokeMessage);
  };

  const handlePokeAlarm = () => {
    pokeMutate(
      {
        receiptOrganizationMemberId: receiverGuest.organizationMemberId,
        pokeMessage: selectedMessage,
      },

      {
        onSuccess: () => {
          success('포키를 성공적으로 보냈어요.');
        },
        onError: (err) => {
          if (err instanceof HttpError) {
            error(err.message);
          }
        },
      }
    );
    onClose();
  };

  return (
    <Modal
      isOpen={isOpen}
      onClose={onClose}
      css={css`
        width: 480px;

        @media (max-width: 500px) {
          width: 90%;
        }
      `}
    >
      <Flex dir="column" gap="16px" margin="24px 0 0 0" alignItems="flex-start">
        <Text type="Heading" weight="semibold">
          {receiverGuest.nickname}에게 포키를 보낼 메시지를 선택해주세요.
        </Text>

        <Grid>
          {Object.entries(POKE_MESSAGES).map(([key, value]) => (
            <Flex
              key={key}
              width="fit-content"
              padding="8px 12px"
              onClick={() => handleClickMessage(key as POKE_MESSAGES_TYPE)}
              css={css`
                border: 1px solid
                  ${selectedMessage === key ? theme.colors.primary500 : theme.colors.gray300};
                border-radius: 16px;
                cursor: pointer;
              `}
            >
              <Text
                role="button"
                color={selectedMessage === key ? theme.colors.primary500 : theme.colors.gray300}
              >
                {value}
              </Text>
            </Flex>
          ))}
        </Grid>
        <Flex justifyContent="space-between" gap="8px" width="100%">
          <Button size="full" color="secondary" onClick={onClose}>
            취소
          </Button>
          <Button size="full" onClick={handlePokeAlarm}>
            전송
          </Button>
        </Flex>
      </Flex>
    </Modal>
  );
};

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  width: 100%;
  gap: 8px;

  @media (max-width: 400px) {
    grid-template-columns: 1fr;
  }
`;
