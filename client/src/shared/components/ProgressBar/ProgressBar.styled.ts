import styled from '@emotion/styled';

import { ProgressBarProps } from './ProgressBar';

type StyledFillProps = {
  percentage: number;
} & Pick<ProgressBarProps, 'color' | 'animated'>;

export const StyledProgressContainer = styled.div<Pick<ProgressBarProps, 'backgroundColor'>>`
  width: 100%;
  height: 8px;
  background-color: ${({ backgroundColor }) => backgroundColor};
  border-radius: 16px;
  overflow: hidden;
  position: relative;
`;

export const StyledProgressFill = styled.div<StyledFillProps>`
  height: 100%;
  width: ${({ percentage }) => `${percentage}%`};
  background-color: ${({ color }) => color};
  border-radius: 16px;
  transition: ${({ animated }) => (animated ? 'width 1.3s ease-in-out' : 'none')};
`;
