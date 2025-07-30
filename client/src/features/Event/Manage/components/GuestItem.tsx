import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { GUEST_STYLES } from '../constants';
import { Guest, NonGuest } from '../types';

type GuestItemProps = {
  guest: Guest | NonGuest;
};

type GuestItemVariant = 'completed' | 'pending';

export const GuestItem = ({ guest }: GuestItemProps) => {
  const isGuest = 'guestId' in guest;
  const variant = isGuest ? 'completed' : 'pending';
  const { badgeTextColor, badgeText } = GUEST_STYLES[variant];

  return (
    <StyledGuestItemContainer
      justifyContent="space-between"
      alignItems="center"
      padding="4px 8px"
      variant={variant}
    >
      <Text type="caption" weight="regular" color={GUEST_STYLES.common.nameTextColor}>
        {guest.nickname}
      </Text>
      <StyledGuestBadge
        alignItems="center"
        gap="8px"
        padding="3.75px 7.8px 4.75px 8px"
        justifyContent="center"
        variant={variant}
      >
        <Text type="caption" weight="regular" color={badgeTextColor}>
          {badgeText}
        </Text>
      </StyledGuestBadge>
    </StyledGuestItemContainer>
  );
};

type GuestItemContainerProps = {
  variant: GuestItemVariant;
};

type GuestBadgeProps = GuestItemContainerProps;

const getContainerStyles = (variant: GuestItemVariant) => {
  if (variant === 'completed') {
    //E.TODO: 추후 클릭 시 모달 로직이 추가되면, 호버 효과(색상) 추가
    return `
      background-color: #F0FDF4;

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
  //E.TODO: 추후 클릭 시 모달 로직이 추가되면, cursor: pointer; 추가
  ${({ variant }) => getContainerStyles(variant)}
`;

export const StyledGuestBadge = styled(Flex)<GuestBadgeProps>`
  border-radius: 6.75px;
  ${({ variant }) => getBadgeStyles(variant)}
`;
