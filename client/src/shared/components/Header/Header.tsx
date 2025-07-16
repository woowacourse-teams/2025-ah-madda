import { ComponentProps, ReactNode } from 'react';

import { StyledHeader } from './Header.styled';

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
  const justifyContent = right ? 'space-between' : 'flex-start';

  return (
    <StyledHeader justifyContent={justifyContent} {...props}>
      {left}
      {right}
    </StyledHeader>
  );
};
