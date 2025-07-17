import { ComponentProps, PropsWithChildren } from 'react';

import { StyledCard } from './Card.styled';

export const Card = ({ children }: PropsWithChildren<ComponentProps<'div'>>) => {
  return <StyledCard>{children}</StyledCard>;
};
