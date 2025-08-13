import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { HttpError } from '@/api/fetcher';
import { usePoke } from '@/api/mutations/usePoke';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Guest, NonGuest } from '../../../Manage/types';

type GuestListProps = {
  eventId: number;
  title: string;
  titleColor: string;
  guests: Guest[] | NonGuest[];
};

export const GuestList = ({ eventId, title, titleColor, guests }: GuestListProps) => {
  const { mutate: pokeMutate } = usePoke(eventId);

  const handlePokeAlarm = (memberId: number) => {
    pokeMutate(
      { receiptOrganizationMemberId: memberId },
      {
        onError: (error) => {
          if (error instanceof HttpError) {
            alert(error.message);
          }
        },
      }
    );
  };

  return (
    <Flex dir="column" margin="40px 0 0 0" padding="0 16px" gap="16px">
      <Flex alignItems="center" gap="8px">
        <Text type="Heading" weight="semibold" color={titleColor}>
          {title}
        </Text>
      </Flex>
      <Flex
        as="ul"
        dir="row"
        gap="8px"
        css={css`
          list-style: none;
          @media (max-width: 768px) {
            flex-direction: column;
          }
        `}
      >
        {guests.map((guest, index) => (
          <GuestBadge key={index} onClick={() => handlePokeAlarm(guest.organizationMemberId)}>
            {guest.nickname}
          </GuestBadge>
        ))}
      </Flex>
    </Flex>
  );
};

const GuestBadge = styled.li`
  width: fit-content;
  background-color: ${theme.colors.gray100};
  color: ${theme.colors.gray600};
  padding: 4px 12px;
  border-radius: 8px;
  cursor: default;

  &:hover {
    background-color: ${theme.colors.gray200};
  }

  @media (max-width: 768px) {
    width: 100%;
    padding: 8px 12px;
    border-radius: 4px;
  }
`;
