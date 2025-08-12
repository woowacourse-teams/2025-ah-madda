import { ComponentProps } from 'react';

import { colorMap, IconColor } from '@/shared/styles/colors';

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
   * @default 'gray'
   */
  color?: IconColor;
} & ComponentProps<'button'>;

export const IconButton = ({ name, size = 20, color, ...props }: IconButtonProps) => {
  const fillColor = colorMap[color ?? 'gray900'];

  return (
    <StyledIconButton type="button" color={fillColor as IconColor} {...props}>
      <Icon name={name} size={size} color={color} />
    </StyledIconButton>
  );
};
