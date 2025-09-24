import { useState } from 'react';

import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { useModal } from '@/shared/hooks/useModal';
import { theme } from '@/shared/styles/theme';

import { Guest, NonGuest } from '../../../Manage/types';

import { PokeModal } from './PokeModal';

type GuestListProps = {
  eventId: number;
  title: string;
  titleColor: string;
  guests: Guest[] | NonGuest[];
};

export const GuestList = ({ eventId, title, titleColor, guests }: GuestListProps) => {
  const { isOpen, open, close } = useModal();
  const [receiverGuest, setReceiverGuest] = useState<NonGuest | null>(null);

  const handleGuestClick = (guest: NonGuest) => {
    setReceiverGuest(guest);
    open();
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
            <GuestBadge key={guest.organizationMemberId} onClick={() => handleGuestClick(guest)}>
              {guest.nickname}
            </GuestBadge>
          ))}
        </Flex>
      </Flex>
      {receiverGuest && (
        <PokeModal
          eventId={eventId}
          receiverGuest={receiverGuest}
          isOpen={isOpen}
          onClose={close}
        />
      )}
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
