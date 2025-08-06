import { keyframes, css } from '@emotion/react';
import styled from '@emotion/styled';

export const StyledToastLayout = styled.div`
  position: fixed;
  top: 25px;
  right: 30px;
  z-index: 1000;
`;

export const StyledToastContainer = styled.div`
  position: relative;
  border-radius: 12px;
  background: #fff;
  padding: 20px 35px 20px 20px;
  margin: 0;
  box-shadow: 0 6px 20px -5px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transform: translateX(0%);
  transition: all 0.5s cubic-bezier(0.68, -0.55, 0.265, 1.35);
  min-width: 280px;
`;

export const StyledToastCloseButton = styled.button`
  position: absolute;
  top: 10px;
  right: 15px;
  padding: 5px;
  background: none;
  border: none;
  cursor: pointer;
  opacity: 0.7;

  &:hover {
    opacity: 1;
  }
`;

const progressKeyframes = keyframes`
  0% {
    right: 0%;
  }
  100% {
    right: 100%;
  }
`;

export const StyledToastProgressBar = styled.div<{
  color: string;
  duration: number;
}>`
  position: absolute;
  bottom: 0;
  left: 0;
  height: 5px;
  width: 100%;

  &::before {
    content: '';
    position: absolute;
    bottom: 0;
    right: 0;
    height: 100%;
    width: 100%;
    background-color: ${({ color }) => color};
    animation: ${({ duration }) => css`
      ${progressKeyframes} ${duration}ms linear forwards
    `};
  }
`;
