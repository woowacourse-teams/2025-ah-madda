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

  /** Show character counter (uses maxLength) */
  showCounter?: boolean;
} & ComponentProps<'textarea'>;

export const Textarea = ({
  helperText,
  errorMessage,
  showCounter = false,
  ...props
}: TextareaProps) => {
  const isError = !!errorMessage;

  const hasMax = typeof props.maxLength === 'number' && props.maxLength > 0;
  const raw = (props.value ?? props.defaultValue ?? '') as string | number;
  const currentLength =
    typeof raw === 'string' ? raw.length : typeof raw === 'number' ? String(raw).length : 0;
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
