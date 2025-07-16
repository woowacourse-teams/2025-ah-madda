import { ComponentPropsWithRef } from 'react';

import { Container, Background, Handle } from './Switch.styled';

export type SwitchProps = {
  checked: boolean;
  onCheckedChange: (checked: boolean) => void;
  disabled?: boolean;
} & Omit<ComponentPropsWithRef<'button'>, 'onClick' | 'type'> & {
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
