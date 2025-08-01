import styled from '@emotion/styled';

import { InputProps } from './Input';

export const StyledWrapper = styled.div`
  width: 100%;
`;

export const StyledLabel = styled.label`
  display: flex;
  align-items: center;
  gap: 4px;
  margin-top: 20px;
  margin-bottom: 8px;
  font-size: 14px;
`;

export const StyledRequiredMark = styled.span`
  color: red;
  font-size: 14px;
`;

export const StyledInput = styled.input<Pick<InputProps, 'error'>>`
  background-color: #f7f7f8;
  border-radius: 8px;
  padding: 12px;
  border: none;
  font-size: 14px;
  width: 100%;

  &:focus {
    outline: none;
    border-color: ${({ error }) => (error ? '#FF5A5A' : '#333')};
  }
`;

export const StyledHelperText = styled.p<Pick<InputProps, 'error'>>`
  margin-top: 4px;
  font-size: 12px;
  min-height: 18px;
  color: ${({ error }) => (error ? '#FF5A5A' : '#888')};
`;
