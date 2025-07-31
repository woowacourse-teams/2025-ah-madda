import { ComponentProps } from 'react';

import { StyledText } from './Text.styled';

export type TextElementType = 'h1' | 'h2' | 'h3' | 'p' | 'label';
type TypographyType = 'Display' | 'Title' | 'Heading' | 'Body' | 'Label';
type WeightType = 'regular' | 'medium' | 'semibold' | 'bold';
export type TextProps<T extends TextElementType> = {
  /**
   * The HTML element or React component to render as.
   * @default 'p'
   */
  as?: T;
  /**
   * The type of text, which can be used for styling purposes.
   */
  type?: TypographyType;
  /**
   * The font weight of the text.
   * Common values include 'regular', 'medium', 'semibold', 'bold'.
   * @default 'regular'
   */
  weight?: WeightType;
  /**
   * The color of the text.
   * This should be a valid CSS color value.
   * For example, it can be a hex code, rgb value, or a named color.
   */
  color?: string;
  /**
   * The content to be displayed inside the text component.
   */
  children: React.ReactNode;
} & Omit<ComponentProps<T>, 'as' | 'children' | 'color'>;

export const Text = <T extends TextElementType = 'p'>({
  as,
  type = 'Body',
  weight = 'regular',
  color = 'black',
  children,
}: TextProps<T>) => {
  return (
    <StyledText as={as} type={type} weight={weight} color={color}>
      {children}
    </StyledText>
  );
};
