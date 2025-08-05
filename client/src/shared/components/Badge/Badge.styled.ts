import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export type BadgeVariant = {
  backgroundColor: string;
  textColor: string;
};

export const BADGE_VARIANTS: Record<string, BadgeVariant> = {
  blue: {
    backgroundColor: `${theme.colors.primary50}`,
    textColor: `${theme.colors.primary600}`,
  },
  gray: {
    backgroundColor: `${theme.colors.gray100}`,
    textColor: `${theme.colors.gray500}`,
  },
  red: {
    backgroundColor: `${theme.colors.red50}`,
    textColor: `${theme.colors.red600}`,
  },
};

export const StyledBadge = styled.div<BadgeVariant>`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px 8px;
  border-radius: 4px;
  background-color: ${({ backgroundColor }) => backgroundColor};
  color: ${({ textColor }) => textColor};
`;
