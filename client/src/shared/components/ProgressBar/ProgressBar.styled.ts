import styled from '@emotion/styled';

export type StyledProgressProps = {
  backgroundColor: string;
};

export type StyledFillProps = {
  width: number;
  color: string;
  animated: boolean;
};

export const ProgressContainer = styled.div<StyledProgressProps>`
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
