import { ComponentPropsWithRef } from 'react';

import { Container, Background, Handle } from './Switch.styled';

export type SwitchProps = {
  /**
   * The current checked state of the switch.
   * @type {boolean}
   * @description Controls whether the switch is in the "on" (true) or "off" (false) position.
   */
  checked: boolean;
  /**
   * Callback function called when the switch state changes.
   * @type {(checked: boolean) => void}
   * @description This function receives the new checked state as a parameter when the user toggles the switch.
   */
  onCheckedChange: (checked: boolean) => void;
  /**
   * Whether the switch is disabled.
   * @type {boolean}
   * @description When disabled, the switch cannot be toggled and appears in a muted visual state.
   * @default false
   */
  disabled?: boolean;
} & Omit<ComponentPropsWithRef<'button'>, 'onClick' | 'type'> & {
    /**
     * The type attribute of the underlying button element.
     * @type {'button'}
     * @description Fixed to 'button' to prevent form submission behavior when used inside forms.
     * @default 'button'
     */
    type?: 'button';
  };

export const Switch = ({
  checked,
  onCheckedChange,
  disabled = false,
  type = 'button',
  ...props
}: SwitchProps) => {
  const handleClick = () => {
    if (!disabled) {
      onCheckedChange(!checked);
    }
  };

  return (
    <Container
      type={type}
      role="switch"
      aria-checked={checked}
      aria-disabled={disabled}
      disabled={disabled}
      onClick={handleClick}
      $checked={checked}
      {...props}
    >
      <Background $checked={checked} $disabled={disabled}>
        <Handle $checked={checked} $disabled={disabled} />
      </Background>
    </Container>
  );
};
