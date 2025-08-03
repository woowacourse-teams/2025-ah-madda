import { StyledBadge } from './Badge.styled';

export type BadgeType = '모집중' | '예정' | '마감';

type BadgeProps = {
  type: BadgeType;
};

export const Badge = ({ type }: BadgeProps) => {
  const getBadgeText = () => {
    switch (type) {
      case '모집중':
        return '모집중';
      case '예정':
        return '예정';
      case '마감':
        return '신청마감';
      default:
        return '모집중';
    }
  };

  return <StyledBadge type={type}>{getBadgeText()}</StyledBadge>;
};
