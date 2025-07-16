import { ComponentPropsWithRef, PropsWithChildren } from 'react';

import { StyledButton } from './Button.styled';

export type ButtonProps = {
  /**
   * The width of the button.
   * @type {string}
   * @description It can be a string (e.g. '100%') or a number (e.g. 100).  1rem = 16px
   * @default '8rem'
   */
  width?: string;
  /**
   * The size of the button.
   * @type {string}
   * @default 'md'
   */
  size?: 'sm' | 'md' | 'lg' | 'xl';
  /**
   * The visual style variant of the button.
   * @type {string}
   * @default 'filled'
   */
  variant?: 'filled' | 'outlined';
  /**
   * The background color of the button.
   * @type {string}
   * @description If possible, use hex codes (e.g., `#808080`) instead of named colors like `gray`,
   * since named colors can't be adjusted with rgba for transparency (e.g., `rgba(gray, 0.5)` won't work).
   * @default '#2563EB'
   */
  color?: string;
  /**
   * The color of the text displayed inside the button.
   * @type {string}
   * @default '#FFFFFF'
   */
  fontColor?: string;
  /**
   * The type of the button.
   * @type {'button' | 'submit' | 'reset'}
   * @default 'button'
   */
  type?: 'button' | 'submit' | 'reset';
} & PropsWithChildren<ComponentPropsWithRef<'button'>>;

// S.TODO: isLoading 추가, 아이콘이 들어가는 경우
export const Button = ({
  width = '8rem',
  size = 'md',
  variant = 'filled',
  color = '#2563EB',
  fontColor = '#FFFFFF',
  type = 'button',
  children,
  ...props
}: ButtonProps) => {
  return (
    <StyledButton
      width={width}
      size={size}
      variant={variant}
      color={color}
      fontColor={fontColor}
      type={type}
      {...props}
    >
      {children}
    </StyledButton>
  );
};
