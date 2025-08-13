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
  const isDateLike = props.type === 'datetime-local';
  const inputRef = useRef<HTMLInputElement>(null);
  const openPicker = () => {
    if (inputRef.current) {
      inputRef.current.showPicker?.();
      inputRef.current.focus();
    }
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

      <StyledHelperText isError={isError}>
        {isError ? (errorMessage ?? ' ') : (helperText ?? ' ')}
      </StyledHelperText>
    </StyledWrapper>
  );
};
