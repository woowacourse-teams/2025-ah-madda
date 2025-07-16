import styled from '@emotion/styled';

import { FlexProps } from './Flex';

export const StyledFlexBox = styled.div<FlexProps>`
  display: flex;
  flex-direction: ${({ dir }) => dir};
  justify-content: ${({ justifyContent }) => justifyContent};
  align-items: ${({ alignItems }) => alignItems};
  gap: ${({ gap }) => gap};
  margin: ${({ margin }) => margin};
  padding: ${({ padding }) => padding};
  width: ${({ width = 'auto' }) => width};
  height: ${({ height = 'auto' }) => height};
`;
