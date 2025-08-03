import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

import { BadgeType } from './Badge';

export const StyledBadge = styled.div<{ type: BadgeType }>`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2px 8px;
  border-radius: 4px;
  font-weight: 600;
  font-size: 13px;
  line-height: 1.5;

  ${({ type }) => {
    switch (type) {
      case '모집중':
        return `
          background-color: ${theme.colors.primary50};
          color: ${theme.colors.primary600};
        `;
      case '예정':
        return `
          background-color: ${theme.colors.gray100};
          color: ${theme.colors.gray500};
        `;
      case '마감':
        return `
          background-color: ${theme.colors.red50};
          color: ${theme.colors.red600};
        `;
      default:
        return `
          background-color: ${theme.colors.gray100};
          color: ${theme.colors.gray500};
        `;
    }
  }}
`;
