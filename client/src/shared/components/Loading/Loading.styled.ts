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

const spin = keyframes`
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
`;

export const StyledLoadingText = styled.span<{ delay: number; size: number }>`
  display: inline-block;
  animation: ${bounce} 1s infinite;
  animation-delay: ${({ delay }) => delay}ms;
  font-size: ${({ size }) => size}px;
  font-weight: bold;
  @media (prefers-reduced-motion: reduce) {
    animation: none;
  }
`;

export const StyledLoadingDots = css`
  display: inline-block;
  margin-left: 4px;
  animation: ${pulse} 1s infinite;
  @media (prefers-reduced-motion: reduce) {
    animation: none;
  }
`;

export const StyledLoadingSpinner = styled.span`
  display: inline-flex;
  align-items: center;
  justify-content: center;

  svg {
    animation: ${spin} 1s linear infinite;
    @media (prefers-reduced-motion: reduce) {
      animation: none;
    }
  }
`;
