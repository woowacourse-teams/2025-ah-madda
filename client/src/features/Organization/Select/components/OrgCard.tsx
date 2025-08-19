import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { Organization } from '../types/Organization';

export type OrgCardProps = {
  org: Organization;
  onJoin: () => void;
  isAdmin: boolean;
  onEdit?: () => void;
};

export const OrgCard = ({ org, onJoin, isAdmin, onEdit }: OrgCardProps) => (
  <StyledCardContainer
    dir="column"
    alignItems="center"
    role="button"
    aria-label={`${org.name} 참여하기`}
    onClick={onJoin}
  >
    <StyledImageWrapper justifyContent="center" alignItems="center">
      <img
        src={org.imageUrl}
        alt={org.name}
        onError={(e) => {
          e.currentTarget.src = '/icon-512x512.png';
        }}
      />

      <StyledOverlay data-overlay>
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
      {org.name}
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
  width: 120px;
`;

const StyledImageWrapper = styled(Flex)`
  width: 120px;
  height: 120px;
  border: 2px solid ${theme.colors.gray100};
  overflow: hidden;
  position: relative;

  img {
    width: 100%;
    height: 100%;
    object-fit: cover;
    display: block;
  }

  &:hover [data-overlay] {
    opacity: 1;
  }
`;

const StyledOverlay = styled.div`
  position: absolute;
  inset: 0;
  display: flex;
  justify-content: center;
  align-items: center;
  background: rgba(0, 0, 0, 0.44);
  opacity: 0;
  transition: opacity 0.18s ease-in-out;
`;
