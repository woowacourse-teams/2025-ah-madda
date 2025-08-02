import { ComponentProps } from 'react';

import {
  StyledWrapper,
  StyledLabel,
  StyledRequiredMark,
  StyledInput,
  StyledHelperText,
} from './Input.styled';

export type InputProps = {
  /**
   * Unique id to link the label and input for accessibility.
   */
  id: string;
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
   * Whether the input is required.
   * When true, a red asterisk (*) will be shown next to the label.
   * This does not trigger browser-native validation unless you also pass it to the DOM via `required`.
   * @default false
   */
  isRequired?: boolean;

  /**
   * Message displayed when the input is invalid.
   */
  errorMessage?: string;
} & ComponentProps<'input'>;

export const Input = ({
  id,
  label,
  helperText,
  isRequired = false,
  errorMessage,
  ...props
}: InputProps) => {
  const isError = !!errorMessage;

  return (
    <StyledWrapper>
      <StyledLabel htmlFor={id}>
        {label}
        {isRequired && <StyledRequiredMark>*</StyledRequiredMark>}
      </StyledLabel>
      <StyledInput id={id} error={isError} {...props} />
      <StyledHelperText error={isError}>
        {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
      </StyledHelperText>
    </StyledWrapper>
  );
};
