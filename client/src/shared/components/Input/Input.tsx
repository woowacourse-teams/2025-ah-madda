import { InputHTMLAttributes } from 'react';

import {
  StyledWrapper,
  StyledLabel,
  StyledRequiredMark,
  StyledInput,
  StyledHelperText,
} from './Input.styled';

export type InputProps = {
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

  /**
   * Whether the input is in an error state.
   * When true, errorMessage will be shown instead of helperText.
   */
  error?: boolean;

  /**
   * Message displayed when the input is invalid.
   */
  errorMessage?: string;
} & InputHTMLAttributes<HTMLInputElement>;

export const Input = ({
  label,
  helperText,
  required = false,
  error = false,
  errorMessage,
  ...props
}: InputProps) => {
  return (
    <StyledWrapper>
      <StyledLabel htmlFor={props.id || props.name}>
        {label}
        {required && <StyledRequiredMark>*</StyledRequiredMark>}
      </StyledLabel>
      <StyledInput {...props} required={required} error={error} />
      {error && errorMessage ? (
        <StyledHelperText error={error}>{errorMessage}</StyledHelperText>
      ) : (
        helperText && <StyledHelperText>{helperText}</StyledHelperText>
      )}
    </StyledWrapper>
  );
};
