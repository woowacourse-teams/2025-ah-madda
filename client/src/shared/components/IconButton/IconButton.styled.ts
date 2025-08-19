import styled from '@emotion/styled';

import { IconButtonProps } from './IconButton';

export const StyledIconButton = styled.button<Pick<IconButtonProps, 'color'>>`
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 6px;
  border: none;
  cursor: pointer;
  background: transparent;
  border-radius: 4px;
  transition: background-color 0.2s ease-in-out;
  color: ${({ color }) => color};

  &:hover {
    background-color: ${({ color }) => `${color}1A`};
  }
`;
