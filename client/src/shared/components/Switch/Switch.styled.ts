import styled from '@emotion/styled';

import { SwitchProps } from './Switch';

type SwitchStateProps = Pick<SwitchProps, 'checked' | 'disabled'>;

export const Background = styled.button<SwitchStateProps>`
  all: unset;
  box-sizing: border-box;

  display: inline-flex;
  align-items: center;
  cursor: pointer;

  position: relative;
  width: 44px;
  height: 24px;
  border-radius: 12px;
  background-color: ${({ checked }) => (checked ? '#000' : '#e5e7eb')};
  transition: background-color 0.2s ease-in-out;

  &:focus-visible {
    outline: 2px solid #2563eb;
    outline-offset: 2px;
  }

  &:disabled {
    opacity: 0.5;
    cursor: not-allowed;
  }

  ${({ disabled }) =>
    disabled &&
    `
    opacity: 0.5;
  `}
`;

export const Handle = styled.div<SwitchStateProps>`
  position: absolute;
  top: 2px;
  left: ${({ checked }) => (checked ? '22px' : '2px')};
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #ffffff;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
  transition: left 0.2s ease-in-out;

  ${({ disabled }) =>
    disabled &&
    `
    opacity: 0.8;
  `}
`;
