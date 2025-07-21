import { Text } from '@/shared/components/Text';

import { Guest } from '../types';

import {
  StyledGuestItemContainer,
  StyledGuestBadge,
  GUEST_STYLES,
  type GuestVariant,
} from './GuestItem.styled';

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
      <Text type="Body" weight="regular" color={GUEST_STYLES.common.nameTextColor}>
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
