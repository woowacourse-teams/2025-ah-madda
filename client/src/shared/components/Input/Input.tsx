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
  title: string;

  /**
   * Helper text displayed below the input field.
   * Useful for showing hints or validation messages.
   * @type {string}
   */
  helperText?: string;
} & InputHTMLAttributes<HTMLInputElement>;

export const Input = ({ title, helperText, required = false, ...props }: InputProps) => {
  return (
    <StyledWrapper>
      <StyledLabel htmlFor={props.id || props.name}>
        {title}
        {required && <StyledRequiredMark>*</StyledRequiredMark>}
      </StyledLabel>
      <StyledInput {...props} required={required} />
      {helperText && <StyledHelperText>{helperText}</StyledHelperText>}
    </StyledWrapper>
  );
};
