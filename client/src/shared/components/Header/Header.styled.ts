import styled from '@emotion/styled';

import { HeaderProps } from './Header';

export const StyledHeader = styled.header`
  position: fixed;
  top: 0;
  width: 100%;
  height: 60px;
  flex-shrink: 0;
  z-index: 10;
  border-bottom: 1px solid #e5e5e5;
  background-color: #fff;
`;

export const StyledHeaderContent = styled.div<Pick<HeaderProps, 'right'>>`
  display: flex;
  justify-content: ${({ right }) => (right ? 'space-between' : 'flex-start')};
  align-items: center;
  height: 100%;
  max-width: 1160px;
  margin: 0 auto;
  width: 100%;
  padding: 0 20px;
`;
