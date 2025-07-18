import { InputHTMLAttributes } from 'react';

import {
  StyledWrapper,
  StyledLabel,
  StyledRequiredMark,
  StyledInput,
  StyledHelperText,
} from './Input.styled';

type InputProps = {
  /**
   * Label text displayed above the input field.
   * @type {string}
   */
  label: string;

  /**
   * Helper text displayed below the input field.
   * Useful for showing hints or validation messages.
   * @type {string}
   */
  helperText?: string;
} & InputHTMLAttributes<HTMLInputElement>;

export const Input = ({ label, helperText, required = false, ...props }: InputProps) => {
  return (
    <StyledWrapper>
      <StyledLabel htmlFor={props.id || props.name}>
        {label}
        {required && <StyledRequiredMark>*</StyledRequiredMark>}
      </StyledLabel>
      <StyledInput {...props} required={required} />
      {helperText && <StyledHelperText>{helperText}</StyledHelperText>}
    </StyledWrapper>
  );
};
