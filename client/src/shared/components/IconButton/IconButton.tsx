import { ComponentProps } from 'react';

import { IconName } from '../Icon/assets';
import { Icon } from '../Icon/Icon';

import { StyledIconButton } from './IconButton.styled';

export type IconButtonProps = {
  /**
   * The name of icon to display.
   */
  name: IconName;
  /**
   * Size of the icon in pixels.
   * @default 20
   */
  size?: number;
  /**
   * The color of the icon.
   * @default '#2B2B2B'
   */
  color: string;
} & ComponentProps<'button'>;

export const IconButton = ({ name, size = 20, color, ...props }: IconButtonProps) => {
  return (
    <StyledIconButton type="button" color={color} {...props}>
      <Icon name={name} size={size} color={color} />
    </StyledIconButton>
  );
};
