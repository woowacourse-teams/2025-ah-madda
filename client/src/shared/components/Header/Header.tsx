import { ComponentProps, ReactNode } from 'react';

import { StyledHeader, StyledHeaderContent } from './Header.styled';

export type HeaderProps = {
  /**
   * The left element of the header.
   * @type {ReactNode}
   * @description Usually used for logo, title, or back button.
   * @required
   */
  left: ReactNode;
  /**
   * The right element of the header.
   * @type {ReactNode}
   * @description Can include icons, profile menus, or action buttons.
   * @optional
   */
  right?: ReactNode;
} & ComponentProps<'header'>;

export const Header = ({ left, right, ...props }: HeaderProps) => {
  return (
    <StyledHeader {...props}>
      <StyledHeaderContent right={!!right}>
        {left}
        {right}
      </StyledHeaderContent>
    </StyledHeader>
  );
};
