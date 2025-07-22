import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { GUEST_STYLES } from '../constants';
import { Guest } from '../types';

export type GuestVariant = 'completed' | 'pending';

type GuestItemProps = {
  guest: Guest;
  onClick: (guest: Guest) => void;
  variant: GuestVariant;
};

export const GuestItem = ({ guest, onClick, variant }: GuestItemProps) => {
  const badgeTextColor = GUEST_STYLES[variant].badgeTextColor;

  return (
    <StyledGuestItemContainer
      justifyContent="space-between"
      alignItems="center"
      onClick={() => onClick(guest)}
      padding="12px 16px"
      variant={variant}
    >
      <Text type="caption" weight="regular" color={GUEST_STYLES.common.nameTextColor}>
        {guest.name}
      </Text>
      <StyledGuestBadge
        alignItems="center"
        gap="8px"
        padding="3.75px 7.8px 4.75px 8px"
        justifyContent="center"
        variant={variant}
      >
        <Text type="caption" weight="regular" color={badgeTextColor}>
          {guest.status}
        </Text>
      </StyledGuestBadge>
    </StyledGuestItemContainer>
  );
};

type GuestItemContainerProps = {
  variant: GuestVariant;
};

type GuestBadgeProps = GuestItemContainerProps;

const getContainerStyles = (variant: GuestVariant) => {
  if (variant === 'completed') {
    return `
      background-color: #F0FDF4;
      &:hover {
        background-color: #E6F2E6;
      }
    `;
  }

  return `
    background-color: #F9FAFB;
    &:hover {
      background-color: #1414140d;
    }
  `;
};

const getBadgeStyles = (variant: GuestVariant) => {
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
  cursor: pointer;
  ${({ variant }) => getContainerStyles(variant)}
`;

export const StyledGuestBadge = styled(Flex)<GuestBadgeProps>`
  border-radius: 6.75px;
  ${({ variant }) => getBadgeStyles(variant)}
`;
