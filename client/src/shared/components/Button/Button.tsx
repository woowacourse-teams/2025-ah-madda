import { ComponentPropsWithRef, PropsWithChildren } from 'react';

import { theme } from '@/shared/styles/theme';

import { Icon } from '../Icon';
import { IconName } from '../Icon/assets';

import { StyledButton } from './Button.styled';

type Variant = 'solid' | 'outline';
type Color = 'primary' | 'secondary' | 'tertiary';
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
} & PropsWithChildren<ComponentPropsWithRef<'button'>>;

// S.TODO: isLoading 추가, 아이콘이 들어가는 경우
export const Button = ({
  size = 'md',
  color = 'primary',
  variant = 'solid',
  iconName,
  type = 'button',
  children,
  ...props
}: ButtonProps) => {
  return (
    <StyledButton
      size={size}
      color={color}
      variant={variant}
      type={type}
      iconName={iconName}
      {...props}
    >
      {size === 'md' && iconName && (
        <Icon
          name={iconName}
          size={20}
          color={variant === 'outline' ? `${theme.colors.gray700}` : 'white'}
        />
      )}
      {children}
    </StyledButton>
  );
};
