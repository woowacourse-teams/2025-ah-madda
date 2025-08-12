import { ComponentProps } from 'react';

import {
  StyledWrapper,
  StyledLabel,
  StyledRequiredMark,
  StyledTextarea,
  StyledHelperText,
} from './Textarea.styled';

export type TextareaProps = {
  /**
   * Unique id to link the label and textarea for accessibility.
   */
  id: string;
  /**
   * Label text displayed above the textarea.
   * @type {string}
   */
  label: string;

  /**
   * Helper text displayed below the textarea.
   * Useful for showing hints or validation messages.
   * @type {string}
   */
  helperText?: string;

  /**
   * Whether the textarea is required.
   * When true, a red asterisk (*) will be shown next to the label.
   * This does not trigger browser-native validation unless you also pass it to the DOM via `required`.
   * @default false
   */
  isRequired?: boolean;

  /**
   * Message displayed when the textarea is invalid.
   */
  errorMessage?: string;
} & ComponentProps<'textarea'>;

export const Textarea = ({
  id,
  label,
  helperText,
  isRequired = false,
  errorMessage,
  ...props
}: TextareaProps) => {
  const isError = !!errorMessage;

  return (
    <StyledWrapper>
      <StyledLabel htmlFor={id}>
        {label}
        {isRequired && <StyledRequiredMark>*</StyledRequiredMark>}
      </StyledLabel>

      <StyledTextarea id={id} isError={isError} aria-invalid={isError} {...props} />

      <StyledHelperText isError={isError}>
        {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
      </StyledHelperText>
    </StyledWrapper>
  );
};
