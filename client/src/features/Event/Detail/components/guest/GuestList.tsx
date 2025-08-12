import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Guest, NonGuest } from '../../../Manage/types';

type GuestListProps = {
  title: string;
  titleColor: string;
  guests: Guest[] | NonGuest[];
};

export const GuestList = ({ title, titleColor, guests }: GuestListProps) => {
  return (
    <Flex dir="column" margin="40px 0 0 0" padding="0 16px" gap="16px">
      <Flex alignItems="center" gap="8px">
        <Text type="Heading" weight="semibold" color={titleColor}>
          {title}
        </Text>
      </Flex>
      <Flex
        dir="row"
        gap="8px"
        css={css`
          @media (max-width: 768px) {
            flex-direction: column;
          }
        `}
      >
        {guests.map((guest, index) => (
          <GuestBadge key={index}>{guest.nickname}</GuestBadge>
        ))}
      </Flex>
    </Flex>
  );
};

const GuestBadge = styled.span`
  width: fit-content;
  background-color: ${theme.colors.gray100};
  color: ${theme.colors.gray600};
  padding: 4px 12px;
  border-radius: 12px;
  cursor: pointer;

  &:hover {
    background-color: ${theme.colors.gray200};
  }
`;
