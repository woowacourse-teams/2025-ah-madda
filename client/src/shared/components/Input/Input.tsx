import { ComponentProps, useRef } from 'react';

import { Icon } from '@/shared/components/Icon';

import {
  StyledWrapper,
  StyledLabel,
  StyledRequiredMark,
  StyledInput,
  StyledHelperText,
  StyledFieldWrapper,
  StyledCalendarButton,
  StyledCounterText,
  StyledFooterRow,
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

  /** Show character counter (uses maxLength) */
  showCounter?: boolean;
} & ComponentProps<'input'>;

export const Input = ({
  id,
  label,
  helperText,
  isRequired = false,
  errorMessage,
  showCounter = false,
  ...props
}: InputProps) => {
  const isError = !!errorMessage;

  const isDateLike = props.type === 'datetime-local';

  const hasMax = typeof props.maxLength === 'number' && props.maxLength > 0;
  const shouldShowCounter = showCounter && hasMax && !isDateLike;

  const raw = (props.value ?? props.defaultValue ?? '') as string | number;
  const currentLength =
    typeof raw === 'string' ? raw.length : typeof raw === 'number' ? String(raw).length : 0;
  const displayLength = hasMax ? Math.min(currentLength, props.maxLength as number) : currentLength;

  const inputRef = useRef<HTMLInputElement>(null);
  const openPicker = () => {
    inputRef.current?.showPicker?.();
    inputRef.current?.focus?.();
  };

  return (
    <StyledWrapper>
      <StyledLabel htmlFor={id}>
        {label}
        {isRequired && <StyledRequiredMark>*</StyledRequiredMark>}
      </StyledLabel>

      <StyledFieldWrapper>
        {isDateLike && (
          <StyledCalendarButton type="button" onClick={openPicker} aria-label="날짜 선택">
            <Icon name="calendar" size={18} />
          </StyledCalendarButton>
        )}
        <StyledInput id={id} ref={inputRef} isError={isError} hasLeftIcon={isDateLike} {...props} />
      </StyledFieldWrapper>

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
