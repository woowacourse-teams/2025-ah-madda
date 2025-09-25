import { css, keyframes } from '@emotion/react';
import styled from '@emotion/styled';

const bounce = keyframes`
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-8px);
  }
`;

const pulse = keyframes`
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.3;
  }
`;

export const StyledLoadingText = styled.span<{ delay: number }>`
  display: inline-block;
  animation: ${bounce} 1s infinite;
  animation-delay: ${({ delay }) => delay}ms;
  font-size: 48px;
  font-weight: bold;
`;

export const StyledLoadingDots = css`
  display: inline-block;
  margin-left: 4px;
  animation: ${pulse} 1s infinite;
`;
