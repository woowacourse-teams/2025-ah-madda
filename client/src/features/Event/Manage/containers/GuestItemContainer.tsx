import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

type GuestItemContainerProps = {
  children: React.ReactNode;
  variant: 'completed' | 'pending';
  clickable?: boolean;
  onClick?: () => void;
};

type StyledGuestItemContainerProps = {
  variant: 'completed' | 'pending';
  clickable?: boolean;
};

export const GuestItemContainer = ({
  children,
  variant,
  clickable,
  onClick,
}: GuestItemContainerProps) => {
  return (
    <StyledGuestItemContainer variant={variant} clickable={clickable} onClick={onClick}>
      {children}
    </StyledGuestItemContainer>
  );
};

const StyledGuestItemContainer = styled.div<StyledGuestItemContainerProps>`
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
