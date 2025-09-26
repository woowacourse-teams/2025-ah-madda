import { keyframes } from '@emotion/react';
import styled from '@emotion/styled';

import { SkeletonProps } from './Skeleton';

const pulse = keyframes`
  0% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
  100% {
    opacity: 1;
  }
`;

export const StyledSkeleton = styled.div<SkeletonProps>`
  display: inline-block;
  width: ${({ width }) => width ?? '100%'};
  height: ${({ height }) => height ?? '12px'};
  border-radius: ${({ borderRadius }) => borderRadius ?? '4px'};
  background-color: ${({ theme }) => theme.colors.gray200};
  animation: ${pulse} 2s cubic-bezier(0.4, 0, 0.6, 1) infinite;
`;
