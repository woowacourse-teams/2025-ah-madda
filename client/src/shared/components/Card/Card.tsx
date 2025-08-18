import { ComponentProps, PropsWithChildren } from 'react';

import { StyledCard } from './Card.styled';

export const Card = ({ children, ...props }: PropsWithChildren<ComponentProps<'div'>>) => {
  return <StyledCard {...props}>{children}</StyledCard>;
};
