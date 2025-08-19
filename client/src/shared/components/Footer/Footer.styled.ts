import styled from '@emotion/styled';

import { theme } from '@/shared/styles/theme';

export const StyledFooterContainer = styled.footer`
  background-color: ${theme.colors.gray50};
  width: 100%;
  height: 20vh;
`;

export const StyledLinkButton = styled.a`
  width: 50px;
  text-decoration: none;

  &:hover {
    text-decoration: underline;
  }
`;
