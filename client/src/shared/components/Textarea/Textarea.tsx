import { ComponentProps } from 'react';

import { computeCounter } from '@/shared/utils/computeCounter';

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

  const { hasMax, displayLength } = computeCounter(
    props.value,
    props.defaultValue,
    props.maxLength
  );

  return (
    <StyledWrapper>
      <StyledTextarea isError={isError} aria-invalid={isError || undefined} {...props} />

      <StyledFooterRow>
        <StyledHelperText isError={isError}>
          {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
        </StyledHelperText>

        {hasMax && (
          <StyledCounterText>
            ({displayLength}/{props.maxLength})
          </StyledCounterText>
        )}
      </StyledFooterRow>
    </StyledWrapper>
  );
};
