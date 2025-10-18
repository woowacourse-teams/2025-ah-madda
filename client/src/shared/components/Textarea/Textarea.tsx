import { ComponentProps } from 'react';

import {
  StyledWrapper,
  StyledTextarea,
  StyledHelperText,
  StyledFooterRow,
  StyledCounterText,
} from './Textarea.styled';

export type TextareaProps = {
  /**
   * Helper text displayed below the textarea.
   */
  helperText?: string;

  /**
   * Message displayed when the textarea is invalid.
   */
  errorMessage?: string;
  /**
   * Force invalid UI on/off (e.g., show only on blur/submit).
   * If omitted, falls back to !!errorMessage.
   */
  isInvalid?: boolean;
} & ComponentProps<'textarea'>;

export const Textarea = ({ helperText, errorMessage, isInvalid, ...props }: TextareaProps) => {
  const isError = isInvalid ?? Boolean(errorMessage);

  return (
    <StyledWrapper>
      <StyledTextarea isError={isError} aria-invalid={isError || undefined} {...props} />

      <StyledFooterRow>
        <StyledHelperText isError={isError}>
          {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
        </StyledHelperText>

        {props.maxLength && (
          <StyledCounterText>
            ({props.value?.toString().length ?? 0}/{props.maxLength})
          </StyledCounterText>
        )}
      </StyledFooterRow>
    </StyledWrapper>
  );
};
