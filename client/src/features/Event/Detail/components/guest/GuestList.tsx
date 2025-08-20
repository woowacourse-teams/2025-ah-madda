import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { HttpError } from '@/api/fetcher';
import { usePoke } from '@/api/mutations/usePoke';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Guest, NonGuest } from '../../../Manage/types';
import { useBouncingMessages } from '../../hooks/useBouncingMessages';

type GuestListProps = {
  eventId: number;
  title: string;
  titleColor: string;
  guests: Guest[] | NonGuest[];
};

export const GuestList = ({ eventId, title, titleColor, guests }: GuestListProps) => {
  const { mutate: pokeMutate } = usePoke(eventId);
  const { bouncingMessages, addBouncingMessage } = useBouncingMessages();

  const handlePokeAlarm = (memberId: number, event: React.MouseEvent) => {
    pokeMutate(
      { receiptOrganizationMemberId: memberId },

      {
        onSuccess: () => {
          addBouncingMessage(event);
        },
        onError: (error) => {
          if (error instanceof HttpError) {
            alert(error.message);
          }
        },
      }
    );
  };

  return (
    <>
      <Flex dir="column" margin="40px 0 0 0" padding="0 16px" gap="16px">
        <Flex alignItems="center" gap="8px">
          <Text type="Heading" weight="semibold" color={titleColor}>
            {title}
          </Text>
        </Flex>
        <Flex
          as="ul"
          dir="row"
          alignItems="flex-start"
          gap="8px"
          css={css`
            flex-wrap: wrap;
            list-style: none;
          `}
        >
          {guests.map((guest) => (
            <GuestBadge
              key={guest.organizationMemberId}
              onClick={(e) => handlePokeAlarm(guest.organizationMemberId, e)}
            >
              {guest.nickname}
            </GuestBadge>
          ))}
        </Flex>
      </Flex>

      {bouncingMessages.map((msg) => (
        <BouncingMessageElement
          key={msg.id}
          css={css`
            left: ${msg.x}px;
            top: ${msg.y}px;
            --move-x: ${msg.moveX}px;
            --move-y: ${msg.moveY}px;
          `}
        >
          {msg.message}
        </BouncingMessageElement>
      ))}
    </>
  );
};

const GuestBadge = styled.li`
  display: inline-block;
  align-items: center;
  width: fit-content;
  height: fit-content;
  background-color: ${theme.colors.gray100};
  color: ${theme.colors.gray600};
  padding: 4px 12px;
  border-radius: 8px;
  cursor: pointer;
  user-select: none;
  -webkit-user-select: none;
`;

const BouncingMessageElement = styled.span`
  position: fixed;
  font-size: 16px;
  font-weight: bold;
  color: ${theme.colors.gray900};
  z-index: 1000;
  pointer-events: none;
  transform: translate(-50%, -50%);
  animation: shootStraight 0.5s ease-out forwards;

  @keyframes shootStraight {
    0% {
      opacity: 1;
      transform: translate(-50%, -50%) scale(1);
    }
    20% {
      opacity: 1;
      transform: translate(-50%, -50%) scale(1.2);
    }
    100% {
      opacity: 0;
      transform: translate(calc(-50% + var(--move-x)), calc(-50% + var(--move-y))) scale(0.5);
    }
  }
`;
