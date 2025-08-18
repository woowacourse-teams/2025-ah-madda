import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Avatar } from '@/shared/components/Avatar';
import { CheckBox } from '@/shared/components/CheckBox';
import { Flex } from '@/shared/components/Flex';
import { theme } from '@/shared/styles/theme';

import { Guest, NonGuest } from '../types';

type GuestItemProps = {
  onGuestChecked: (organizationMemberId: number) => void;
  guest: Guest | NonGuest;
  onGuestClick?: (guest: Guest | NonGuest) => void;
};

type GuestItemVariant = 'completed' | 'pending';

type GuestItemContainerProps = {
  variant: GuestItemVariant;
  clickable?: boolean;
};

export const GuestItem = ({ guest, onGuestChecked, onGuestClick }: GuestItemProps) => {
  const isGuest = 'guestId' in guest;
  const variant = isGuest ? 'completed' : 'pending';

  const handleGuestClick = () => {
    if (onGuestClick && isGuest) {
      onGuestClick(guest);
    }
  };

  return (
    <GuestItemContainer variant={variant} clickable={isGuest} onClick={handleGuestClick}>
      <Flex gap="18px" alignItems="center" width="100%">
        <CheckBox
          checked={guest.isChecked}
          onClick={(e) => {
            e.stopPropagation();
            onGuestChecked(guest.organizationMemberId);
          }}
        />
        <Avatar
          picture={null}
          name={guest.nickname}
          css={css`
            gap: 12px;
          `}
        />
      </Flex>
    </GuestItemContainer>
  );
};

const GuestItemContainer = styled.div<GuestItemContainerProps>`
  display: flex;
  align-items: center;
  padding: 12px 24px;
  border-radius: 8px;
  width: 100%;
  transition: all 0.2s ease;

  ${({ variant }) => {
    if (variant === 'completed') {
      return `
        background-color: ${theme.colors.gray50};
      `;
    }
    return `
      background-color: ${theme.colors.white};
    `;
  }}

  ${({ clickable }) =>
    clickable &&
    `
    cursor: pointer;
    
    &:hover {
      background-color: ${theme.colors.gray100};
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    `}
`;
