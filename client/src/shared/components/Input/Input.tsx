import { ComponentProps, useRef } from 'react';

import { Icon } from '@/shared/components/Icon';

import {
  StyledWrapper,
  StyledInput,
  StyledHelperText,
  StyledFieldWrapper,
  StyledCalendarButton,
  StyledClearButton,
  StyledCounterText,
  StyledFooterRow,
} from './Input.styled';

export type InputProps = {
  /**
   * Unique id to link the label and input for accessibility.
   */
  id: string;
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
  /**
   * Whether to show the character counter.
   * When true, the character counter will be shown below the input.
   * @default false
   */
  showCounter?: boolean;
  /**
   * Callback function when clear button is clicked.
   * When provided, a clear button will be shown on the right side of the input.
   */
  onClear?: () => void;
} & ComponentProps<'input'>;

export const Input = ({
  id,
  helperText,
  isRequired = false,
  errorMessage,
  showCounter = false,
  onClear,
  ...props
}: InputProps) => {
  const isError = !!errorMessage;
  const isDateLike = props.type === 'datetime-local';
  const shouldShowCounter = showCounter && props.maxLength && !isDateLike;

  const inputRef = useRef<HTMLInputElement>(null);
  const openPicker = () => {
    inputRef.current?.showPicker?.();
    inputRef.current?.focus?.();
  };

  return (
    <StyledWrapper>
      <StyledFieldWrapper>
        {isDateLike && (
          <StyledCalendarButton type="button" onClick={openPicker} aria-label="날짜 선택">
            <Icon name="calendar" size={18} />
          </StyledCalendarButton>
        )}
        <StyledInput
          id={id}
          ref={inputRef}
          isError={isError}
          hasLeftIcon={isDateLike}
          hasRightIcon={!!onClear}
          aria-required={isRequired || undefined}
          aria-invalid={isError || undefined}
          {...props}
        />
        {onClear && props.value && (
          <StyledClearButton type="button" onClick={onClear} aria-label="초기화">
            <Icon name="close" size={16} color="gray500" />
          </StyledClearButton>
        )}
      </StyledFieldWrapper>

      <StyledFooterRow>
        <StyledHelperText isError={isError}>
          {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
        </StyledHelperText>

        {shouldShowCounter && (
          <StyledCounterText>
            ({props.value?.toString().length ?? 0}/{props.maxLength})
          </StyledCounterText>
        )}
      </StyledFooterRow>
    </StyledWrapper>
  );
};
