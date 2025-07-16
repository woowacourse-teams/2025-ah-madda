import { ElementType, HTMLAttributes } from 'react';

import { StyledFlexBox } from './Flex.styled';

export type FlexProps = {
  /**
   * The HTML tag or React component to render as the flex container.
   * @type {ElementType}
   * @description Allows polymorphic behavior - can be any HTML element (e.g., 'div', 'section', 'article') or React component.
   * @default 'div'
   */
  as?: ElementType;
  /**
   * The flex direction of the container.
   * @type {'row' | 'row-reverse' | 'column' | 'column-reverse'}
   * @description Sets the main axis direction for flex items.
   * @default 'row'
   */
  dir?: 'row' | 'row-reverse' | 'column' | 'column-reverse';
  /**
   * The alignment of flex items along the main axis.
   * @type {string}
   * @description Controls how flex items are distributed along the main axis.
   * @default 'flex-start'
   */
  justifyContent?:
    | 'flex-start'
    | 'flex-end'
    | 'center'
    | 'space-between'
    | 'space-around'
    | 'space-evenly';
  /**
   * The alignment of flex items along the cross axis.
   * @type {string}
   * @description Controls how flex items are aligned perpendicular to the main axis.
   * @default 'stretch'
   */
  alignItems?: 'flex-start' | 'flex-end' | 'center' | 'stretch' | 'baseline';
  /**
   * The gap between flex items.
   * @type {string}
   * @description Sets the spacing between flex items. Numbers are converted to px.
   * @default 0
   */
  gap?: string;
  /**
   * The margin around the flex container.
   * @type {string}
   * @description Sets margin on all sides. Numbers are converted to px.
   * @default 0
   */
  margin?: string;
  /**
   * The padding inside the flex container.
   * @type {string}
   * @description Sets padding on all sides. Numbers are converted to px.
   * @default 0
   */
  padding?: string;
  /**
   * The width of the flex container.
   * @type {string}
   * @description Sets the width (e.g., '100%', '300px', 'auto').
   * @default 'auto'
   */
  width?: string;
  /**
   * The height of the flex container.
   * @type {string}
   * @description Sets the height (e.g., '100vh', '200px', 'auto').
   * @default 'auto'
   */
  height?: string;
} & HTMLAttributes<HTMLDivElement>;

export const Flex = ({
  as = 'div',
  dir = 'row',
  justifyContent = 'flex-start',
  alignItems = 'stretch',
  gap,
  margin,
  padding,
  width = 'auto',
  height = 'auto',
  children,
  ...props
}: FlexProps) => {
  return (
    <StyledFlexBox
      as={as}
      direction={dir}
      justifyContent={justifyContent}
      alignItems={alignItems}
      gap={gap}
      margin={margin}
      padding={padding}
      width={width}
      height={height}
      {...props}
    >
      {children}
    </StyledFlexBox>
  );
};
