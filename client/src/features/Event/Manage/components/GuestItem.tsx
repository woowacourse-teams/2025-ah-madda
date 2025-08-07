import styled from '@emotion/styled';

import { CheckBox } from '@/shared/components/CheckBox';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { GUEST_STYLES } from '../constants';
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
    <Flex width="100%" gap="8px" alignItems="center" padding="0 0 0 20px">
      <Flex width="30px">
        <CheckBox
          checked={guest.isChecked}
          onClick={() => onGuestChecked(guest.organizationMemberId)}
        />
      </Flex>
      <StyledGuestItemContainer
        variant={variant}
        alignItems="center"
        padding="12px"
        onClick={handleGuestClick}
        clickable={isGuest}
      >
        <Text type="Label" weight="regular" color={GUEST_STYLES.common.nameTextColor}>
          {guest.nickname}
        </Text>
      </StyledGuestItemContainer>
    </Flex>
  );
};

const getContainerStyles = (variant: GuestItemVariant) => {
  if (variant === 'completed') {
    return `
      background-color: #F0FDF4;
    `;
  }

  return `
    background-color: #F9FAFB;
  `;
};

export const StyledGuestItemContainer = styled(Flex)<GuestItemContainerProps>`
  width: 100%;
  border-radius: 8px;
  ${({ variant }) => getContainerStyles(variant)}
  ${({ clickable }) =>
    clickable &&
    `
    cursor: pointer;
    transition: all 0.2s ease;
    
    &:hover {
      background-color: #DCFCE7;
      transform: translateY(-1px);
      box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    }
    `}
`;
