import { StyledSpacing } from './Spacing.styled';

export type SpacingProps = {
  /**
   * Height of the spacing element.
   * @default '1px'
   */
  height?: string;
  /**
   * Background color of the spacing element.
   * @default 'transparent'
   */
  color?: string;
};
export const Spacing = ({ height, color, ...props }: SpacingProps) => {
  return <StyledSpacing height={height} color={color} {...props} />;
};
