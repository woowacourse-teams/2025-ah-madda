import { ComponentProps } from 'react';

import { StyledWrapper, StyledTextarea, StyledHelperText } from './Textarea.styled';

export type TextareaProps = {
  /**
   * Helper text displayed below the textarea.
   * Useful for showing hints or validation messages.
   * @type {string}
   */
  helperText?: string;

  /**
   * Message displayed when the textarea is invalid.
   */
  errorMessage?: string;
} & ComponentProps<'textarea'>;

export const Textarea = ({ id, helperText, errorMessage, ...props }: TextareaProps) => {
  const isError = !!errorMessage;

  return (
    <StyledWrapper>
      <StyledTextarea id={id} isError={isError} aria-invalid={isError} {...props} />
      <StyledHelperText isError={isError}>
        {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
      </StyledHelperText>
    </StyledWrapper>
  );
};
