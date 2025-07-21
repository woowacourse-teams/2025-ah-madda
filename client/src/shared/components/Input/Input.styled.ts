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
  width: 100%;
  padding: 8px 12px;
  font-size: 14px;
  border: 1px solid ${({ error }) => (error ? '#FF5A5A' : '#ccc')};
  border-radius: 6px;
  box-sizing: border-box;
  background: white;
  color: black;

  &:focus {
    outline: none;
    border-color: ${({ error }) => (error ? '#FF5A5A' : '#333')};
  }
`;

export const StyledHelperText = styled.p<Pick<InputProps, 'error'>>`
  margin-top: 4px;
  font-size: 12px;
  color: ${({ error }) => (error ? '#FF5A5A' : '#888')};
`;
