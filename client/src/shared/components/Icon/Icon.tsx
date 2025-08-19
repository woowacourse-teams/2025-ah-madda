import { SVGProps } from 'react';

import { colorMap, IconColor } from '@/shared/styles/colors';

import { IconName, Icons } from './assets';

type IconProps = {
  /**
   * Name of the icon to render.
   * Must be one of the keys in Icons.
   */
  name: IconName;
  /**
   * Size of the icon in pixels.
   * @default 24
   */
  size?: number;
  /**
   * Color of the icon.
   * @default 'gray'
   */
  color?: IconColor;
} & SVGProps<SVGSVGElement>;

export const Icon = ({ name, size, color, ...props }: IconProps) => {
  const SvgIcon = Icons[name];
  const fillColor = colorMap[color ?? 'gray'];

  return <SvgIcon width={size} height={size} color={fillColor} {...props} />;
};
