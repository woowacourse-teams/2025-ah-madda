import styled from '@emotion/styled';

import { ProgressBarProps } from './ProgressBar';

type StyledFillProps = {
  width: number;
} & Pick<ProgressBarProps, 'color' | 'animated'>;

export const ProgressContainer = styled.div<Pick<ProgressBarProps, 'backgroundColor'>>`
  width: 100%;
  height: 8px;
  background-color: ${({ backgroundColor }) => backgroundColor};
  border-radius: 16px;
  overflow: hidden;
  position: relative;
`;

export const ProgressFill = styled.div<StyledFillProps>`
  height: 100%;
  width: ${({ width }) => `${width}%`};
  background-color: ${({ color }) => color};
  border-radius: 16px;
  transition: ${({ animated }) => (animated ? 'width 1.3s ease-in-out' : 'none')};
`;
