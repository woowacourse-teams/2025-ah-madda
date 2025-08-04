import { ReactNode } from 'react';

import { Text } from '@/shared/components/Text';

import { BADGE_VARIANTS, StyledBadge } from './Badge.styled';

type BadgeProps = {
  variant: keyof typeof BADGE_VARIANTS;
  children: ReactNode;
};

export const Badge = ({ variant = 'blue', children, ...props }: BadgeProps) => {
  const style = BADGE_VARIANTS[variant];

  return (
    <StyledBadge backgroundColor={style.backgroundColor} textColor={style.textColor} {...props}>
      <Text type="Label" weight="semibold" color={style.textColor}>
        {children}
      </Text>
    </StyledBadge>
  );
};
