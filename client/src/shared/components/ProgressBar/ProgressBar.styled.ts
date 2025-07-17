import styled from '@emotion/styled';

export type StyledProgressProps = {
  height: string;
  backgroundColor: string;
  borderRadius: string;
};

export type StyledFillProps = {
  width: number;
  color: string;
  borderRadius: string;
  animated: boolean;
};

export const ProgressContainer = styled.div<StyledProgressProps>`
  width: 100%;
  height: ${({ height }) => height};
  background-color: ${({ backgroundColor }) => backgroundColor};
  border-radius: ${({ borderRadius }) => borderRadius};
  overflow: hidden;
  position: relative;
`;

export const ProgressFill = styled.div<StyledFillProps>`
  height: 100%;
  width: ${({ width }) => `${width}%`};
  background-color: ${({ color }) => color};
  border-radius: ${({ borderRadius }) => borderRadius};
  transition: ${({ animated }) => (animated ? 'width 1.3s ease-in-out' : 'none')};
`;
