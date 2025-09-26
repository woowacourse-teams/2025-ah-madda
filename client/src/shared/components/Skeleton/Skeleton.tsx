import { StyledSkeleton } from './Skeleton.styled';

export type SkeletonProps = {
  /**
   * width of the skeleton. Can be a string (e.g., '100%', '50px', '10rem').
   * @default '100%'.
   */
  width?: string;
  /**
   * height of the skeleton. Can be a string (e.g., '100%', '50px', '10rem').
   * @default '12px'.
   */
  height?: string;
  /**
   * border radius of the skeleton. Can be a string (e.g., '100%', '50px', '10rem').
   * @default '4px'.
   */
  borderRadius?: string;
};

export const Skeleton = ({
  width = '100%',
  height = '12px',
  borderRadius = '4px',
}: SkeletonProps) => {
  return <StyledSkeleton width={width} height={height} borderRadius={borderRadius} />;
};
