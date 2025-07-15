import { ElementType, HTMLAttributes } from 'react';

import { FlexStyle, StyledFlex } from './Flex.styled';

type FlexProps = HTMLAttributes<HTMLDivElement> &
  FlexStyle & {
    tag?: ElementType;
  };

export const Flex = ({ tag = 'div', children, ...props }: FlexProps) => {
  return (
    <StyledFlex as={tag} {...props}>
      {children}
    </StyledFlex>
  );
};
