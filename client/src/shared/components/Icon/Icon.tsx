import { SVGProps } from 'react';

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
   * @default '#2B2B2B'
   */
  color?: string;
} & SVGProps<SVGSVGElement>;

export const Icon = ({ name, size, color, ...props }: IconProps) => {
  const SvgIcon = Icons[name];
  return <SvgIcon width={size} height={size} color={color} {...props} />;
};
