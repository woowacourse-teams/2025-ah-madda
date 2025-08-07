import styled from '@emotion/styled';

import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { GUEST_STYLES } from '../../Manage/constants';
import { Guest, NonGuest } from '../../Manage/types';

type GuestItemProps = {
  guest: Guest | NonGuest;
};

type GuestItemVariant = 'completed' | 'pending';

export const GuestItem = ({ guest }: GuestItemProps) => {
  const isGuest = 'guestId' in guest;
  const variant = isGuest ? 'completed' : 'pending';

  return (
    <Flex width="100%" gap="8px" alignItems="center">
      <StyledGuestItemContainer variant={variant} alignItems="center" padding="12px">
        <Text type="Label" weight="regular" color={GUEST_STYLES.common.nameTextColor}>
          {guest.nickname}
        </Text>
      </StyledGuestItemContainer>
    </Flex>
  );
};

type GuestItemContainerProps = {
  variant: GuestItemVariant;
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
`;
