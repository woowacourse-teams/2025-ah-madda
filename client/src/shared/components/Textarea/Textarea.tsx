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

  /** Show character counter (uses maxLength) */
  showCounter?: boolean;
} & ComponentProps<'textarea'>;

export const Textarea = ({
  helperText,
  errorMessage,
  isInvalid,
  showCounter = false,
  ...props
}: TextareaProps) => {
  const isError = isInvalid ?? Boolean(errorMessage);

  const hasMax = typeof props.maxLength === 'number' && props.maxLength > 0;
  const rawValue = props.value ?? props.defaultValue ?? '';
  const currentLength = String(rawValue).length;
  const displayLength = hasMax ? Math.min(currentLength, props.maxLength as number) : currentLength;

  const shouldShowCounter = showCounter && hasMax;

  return (
    <StyledWrapper>
      <StyledTextarea isError={isError} aria-invalid={isError || undefined} {...props} />

      <StyledFooterRow>
        <StyledHelperText isError={isError}>
          {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
        </StyledHelperText>

        {shouldShowCounter && (
          <StyledCounterText>
            ({displayLength}/{props.maxLength})
          </StyledCounterText>
        )}
      </StyledFooterRow>
    </StyledWrapper>
  );
};
