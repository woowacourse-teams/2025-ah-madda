import styled from '@emotion/styled';

export const ProgressContainer = styled.div<{
  height: string;
  backgroundColor: string;
  borderRadius: string;
}>`
  width: 100%;
  height: ${(props) => props.height};
  background-color: ${(props) => props.backgroundColor};
  border-radius: ${(props) => props.borderRadius};
  overflow: hidden;
  position: relative;
`;

export const ProgressFill = styled.div<{
  width: number;
  color: string;
  borderRadius: string;
  animated: boolean;
}>`
  height: 100%;
  width: ${(props) => props.width}%;
  background-color: ${(props) => props.color};
  border-radius: ${(props) => props.borderRadius};
  transition: ${(props) => (props.animated ? 'width 1.3s ease-in-out' : 'none')};
`;
