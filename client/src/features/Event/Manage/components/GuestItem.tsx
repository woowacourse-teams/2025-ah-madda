import styled from '@emotion/styled';

import { CheckBox } from '@/shared/components/CheckBox';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';

import { GUEST_STYLES } from '../constants';
import { Guest, NonGuest } from '../types';

type GuestItemProps = {
  onGuestChecked: (organizationMemberId: number) => void;
  guest: Guest | NonGuest;
};

type GuestItemVariant = 'completed' | 'pending';

export const GuestItem = ({ guest, onGuestChecked }: GuestItemProps) => {
  const isGuest = 'guestId' in guest;
  const variant = isGuest ? 'completed' : 'pending';

  return (
    <Flex width="100%" gap="8px" alignItems="center" padding="0 0 0 20px">
      <Flex width="30px">
        <CheckBox
          checked={guest.isChecked}
          onClick={() => onGuestChecked(guest.organizationMemberId)}
        />
      </Flex>
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
    //E.TODO: 추후 클릭 시 모달 로직이 추가되면, 호버 효과(색상) 추가
    return `
      background-color: #F0FDF4;
    `;
  }

  return `
    background-color: #F9FAFB;
  `;
};

//E.TODO: 추후 클릭 시 모달 로직이 추가되면, cursor: pointer; 추가
export const StyledGuestItemContainer = styled(Flex)<GuestItemContainerProps>`
  width: 100%;
  border-radius: 8px;
  ${({ variant }) => getContainerStyles(variant)}
`;
