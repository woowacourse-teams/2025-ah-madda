import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import type { Organization } from '../../Organization/types/Organization';

export type OrgCardProps = {
  organization: Organization;
  onJoin: () => void;
  isAdmin: boolean;
  onEdit?: () => void;
};

export const OrgCard = ({ organization, onJoin, isAdmin, onEdit }: OrgCardProps) => (
  <StyledCardContainer
    dir="column"
    alignItems="center"
    role="button"
    aria-label={`${organization.name} 참여하기`}
    onClick={onJoin}
  >
    <StyledImageWrapper justifyContent="center" alignItems="center">
      <StyledImg
        src={organization.imageUrl}
        alt={organization.name}
        onError={(e) => {
          e.currentTarget.src = '/icon-512x512.png';
        }}
        width={255}
        height={255}
      />

      <StyledOverlay data-overlay justifyContent="center" alignItems="center">
        <Text color="white" weight="semibold">
          참여하기
        </Text>
      </StyledOverlay>
    </StyledImageWrapper>

    <Text
      css={css`
        margin-top: 8px;
        text-align: center;
      `}
    >
      {organization.name}
    </Text>

    {isAdmin && onEdit && (
      <Button
        color="secondary"
        size="sm"
        onClick={(e) => {
          e.stopPropagation();
          onEdit();
        }}
        css={css`
          margin-top: 4px;
        `}
      >
        수정
      </Button>
    )}
  </StyledCardContainer>
);

const StyledCardContainer = styled(Flex)`
  cursor: pointer;
  width: 175px;
`;

const StyledImageWrapper = styled(Flex)`
  border-radius: 12px;
  border: 1px solid ${theme.colors.gray100};
  width: clamp(140px, 30vw, 175px);
  height: clamp(140px, 30vw, 175px);
  position: relative;
  overflow: hidden;

  &:hover [data-overlay] {
    opacity: 1;
  }
`;

const StyledOverlay = styled(Flex)`
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.44);
  opacity: 0;
  transition: opacity 0.18s ease-in-out;
  border-radius: 12px;
`;

const StyledImg = styled.img`
  width: 100%;
  height: 100%;
  object-fit: scale-down;
  display: block;
`;
