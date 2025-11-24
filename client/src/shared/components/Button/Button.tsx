import { ComponentPropsWithRef, PropsWithChildren } from 'react';

import { IconColor } from '@/shared/styles/colors';

import { Icon } from '../Icon';
import { IconName } from '../Icon/assets';
import { Loading } from '../Loading';

import { StyledButton } from './Button.styled';

type Variant = 'solid' | 'ghost' | 'outline';
export type Color = 'primary' | 'secondary' | 'tertiary';
export type ButtonProps = {
  /**
   * The size of the button.
   * @type {string}
   * @default 'md'
   */
  size?: 'sm' | 'md' | 'lg' | 'full';
  /**
   * The color scheme of the button.
   * @type {string}
   * @default 'primary'
   */
  color?: Color;
  /**
   * The visual style variant of the button.
   * @type {string}
   * @default 'solid'
   */
  variant?: Variant;
  /**
   * The name of the icon to display in the button.
   * If provided, the button will render an icon.
   * @type {IconName}
   */
  iconName?: IconName;
  /**
   * The type of the button.
   * @type {'button' | 'submit' | 'reset'}
   * @default 'button'
   */
  type?: 'button' | 'submit' | 'reset';
  /**
   * Whether the button is loading.
   * @type {boolean}
   * @default false
   */
  isLoading?: boolean;
} & PropsWithChildren<ComponentPropsWithRef<'button'>>;

const ICON_SIZE_MAP = {
  sm: 14,
  md: 16,
  lg: 20,
  full: 16,
} as const;

export const Button = ({
  size = 'md',
  color = 'primary',
  variant = 'solid',
  iconName,
  type = 'button',
  children,
  isLoading = false,
  ...props
}: ButtonProps) => {
  const isDisabled = isLoading || props.disabled;

  return (
    <StyledButton
      size={size}
      color={color}
      variant={variant}
      type={type}
      iconName={iconName}
      disabled={isDisabled}
      {...props}
    >
      {isLoading && (
        <Loading type="spinner" size={ICON_SIZE_MAP[size]} color={getIconColor(variant, color)} />
      )}
      {size === 'md' && iconName && (
        <Icon name={iconName} size={20} color={getIconColor(variant, color)} />
      )}
      {children}
    </StyledButton>
  );
};

const getIconColor = (variant: Variant, color: Color) => {
  if (variant === 'outline') return 'gray500';
  if (variant === 'ghost') {
    const ghostColorMap: Record<Color, IconColor> = {
      primary: 'primary',
      secondary: 'secondary',
      tertiary: 'gray',
    };
    return ghostColorMap[color];
  }
  return color === 'secondary' ? 'primary' : 'white';
};
