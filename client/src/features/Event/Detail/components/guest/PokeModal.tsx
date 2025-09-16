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

        <Flex width="100%" justifyContent="center">
          <Text type="Body" weight="semibold" color={theme.colors.primary500}>
            아맞다님이 {POKE_MESSAGES[selectedMessage]}
          </Text>
        </Flex>

        <Grid>
          {Object.entries(POKE_MESSAGES).map(([key, value]) => (
            <Segment
              key={key}
              onClick={() => handleClickMessage(key as POKE_MESSAGES_TYPE)}
              isSelected={selectedMessage === key}
            >
              <Text
                role="button"
                color={selectedMessage === key ? theme.colors.primary500 : theme.colors.gray300}
              >
                {value}
              </Text>
            </Segment>
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

const Segment = styled.button<{ isSelected: boolean }>`
  all: unset;
  width: 100%;
  max-width: 190px;
  word-break: keep-all;
  border: 1px solid
    ${(props) => (props.isSelected ? theme.colors.primary500 : theme.colors.gray300)};
  text-align: center;
  border-radius: 12px;
  cursor: pointer;
  padding: 8px 12px;
`;

const Grid = styled.div`
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  width: 100%;
  justify-items: center;
  gap: 8px;

  @media (max-width: 500px) {
    grid-template-columns: 1fr;
  }
`;
