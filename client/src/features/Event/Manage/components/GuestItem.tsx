import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { GUEST_STYLES } from '../constants';
import { Guest, NonGuest } from '../types';

type GuestItemProps = {
  guest: Guest | NonGuest;
  onClick?: (guest: Guest | NonGuest) => void;
};

type GuestItemVariant = 'completed' | 'pending';

export const GuestItem = ({ guest, onClick }: GuestItemProps) => {
  const isGuest = 'guestId' in guest;
  const variant = isGuest ? 'completed' : 'pending';
  const { badgeTextColor, badgeText } = GUEST_STYLES[variant];

  return (
    <StyledGuestItemContainer
      justifyContent="space-between"
      alignItems="center"
      padding="4px 8px"
      variant={variant}
      isClickable={!!onClick}
      onClick={() => onClick?.(guest)}
    >
      <Text type="Label" weight="regular" color={GUEST_STYLES.common.nameTextColor}>
        {guest.nickname}
      </Text>
      <StyledGuestBadge
        alignItems="center"
        gap="8px"
        padding="3.75px 7.8px 4.75px 8px"
        justifyContent="center"
        variant={variant}
      >
        <Text type="Label" weight="regular" color={badgeTextColor}>
          {badgeText}
        </Text>
      </StyledGuestBadge>
    </StyledGuestItemContainer>
  );
};

type GuestItemContainerProps = {
  variant: GuestItemVariant;
  isClickable?: boolean;
};

type GuestBadgeProps = GuestItemContainerProps;

const getContainerStyles = (variant: GuestItemVariant, isClickable: boolean) => {
  if (variant === 'completed') {
    return `
      background-color: #F0FDF4;
      ${
        isClickable
          ? `
        cursor: pointer;
        transition: background-color 0.2s ease;

        &:hover {
          background-color: #DCFCE7;
        }
      `
          : ''
      }
    `;
  }

  return `
    background-color: #F9FAFB;
  `;
};

const getBadgeStyles = (variant: GuestItemVariant) => {
  if (variant === 'completed') {
    return `
      background: #DCFCE7;
    `;
  }

  return `
    background: #ECEEF2;
  `;
};

export const StyledGuestItemContainer = styled(Flex)<GuestItemContainerProps>`
  border-radius: 8px;
  ${({ variant, isClickable = false }) => getContainerStyles(variant, isClickable)}
`;

export const StyledGuestBadge = styled(Flex)<GuestBadgeProps>`
  border-radius: 6.75px;
  ${({ variant }) => getBadgeStyles(variant)}
`;
