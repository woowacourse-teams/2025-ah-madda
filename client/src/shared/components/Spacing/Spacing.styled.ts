import styled from '@emotion/styled';

import { SpacingProps } from './Spacing';

export const StyledSpacing = styled.div<SpacingProps>`
  width: 100%;
  height: ${({ height }) => height || '1px'};
  background-color: ${({ color }) => color || 'transparent'};
`;
