import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';

export type GuestVariant = 'completed' | 'pending';

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

export const GUEST_STYLES = {
  completed: {
    badgeTextColor: '#4CAF50',
  },
  pending: {
    badgeTextColor: '#666',
  },
  common: {
    nameTextColor: '#0A0A0A',
  },
} as const;
