import { css } from '@emotion/react';
import styled from '@emotion/styled';

import { OrganizationAPIResponse } from '@/api/types/organizations';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

export type OrgCardProps = {
  organization: OrganizationAPIResponse;
  onJoin: () => void;
  position: number;
  total: number;
};

export const DEFAULT_IMAGE_SIZE = 120;
export const OrgCard = ({ organization, onJoin, position, total }: OrgCardProps) => {
  const { name, description = '', imageUrl } = organization;

  const ariaLabel = `${total}개의 스페이스 중 ${position}번째 스페이스입니다. 스페이스 이름은 ${name}입니다.${
    description ? ` 스페이스 설명은 ${description}입니다.` : ' 설명 정보가 없습니다.'
  } 엔터 키를 눌러 구경하세요.`;

  const handleKeyDown: React.KeyboardEventHandler<HTMLDivElement> = (e) => {
    if (e.key === 'Enter' || e.key === ' ') {
      e.preventDefault();
      onJoin();
    }
  };

  return (
    <StyledCardContainer
      dir="row"
      alignItems="center"
      role="listitem"
      aria-roledescription="button"
      tabIndex={0}
      width="100%"
      gap="4px"
      aria-label={ariaLabel}
      onClick={onJoin}
      onKeyDown={handleKeyDown}
    >
      <StyledImageWrapper
        justifyContent="center"
        alignItems="center"
        width={`${DEFAULT_IMAGE_SIZE}px`}
        height={`${DEFAULT_IMAGE_SIZE}px`}
      >
        <StyledImg
          src={imageUrl}
          alt={name}
          onError={(e) => {
            e.currentTarget.onerror = null;
            e.currentTarget.src = '/icon-192x192.png';
          }}
        />
      </StyledImageWrapper>
      <Flex dir="column" gap="4px" justifyContent="center" alignItems="flex-start" width="100%">
        <Text type="Heading" weight="bold" aria-hidden="true">
          {name}
        </Text>
        <Text
          type="Body"
          color={theme.colors.gray500}
          aria-hidden="true"
          css={css`
            display: -webkit-box;
            -webkit-line-clamp: 2;
            -webkit-box-orient: vertical;
            overflow: hidden;
            line-height: 1.4;
            word-break: break-word;
          `}
        >
          {description}
        </Text>
      </Flex>

      <StyledOverlay data-overlay justifyContent="center" alignItems="center" aria-hidden="true">
        <Text type="Heading" color="white" weight="semibold">
          구경하기
        </Text>
      </StyledOverlay>
    </StyledCardContainer>
  );
};

const StyledCardContainer = styled(Flex)`
  cursor: pointer;
  position: relative;
  border-radius: 12px;
  border: 1px solid ${theme.colors.gray100};

  &:hover [data-overlay] {
    opacity: 1;
  }

  &:focus-visible {
    outline: 3px solid ${theme.colors.primary400};
    outline-offset: 2px;
  }
`;

const StyledImageWrapper = styled(Flex)`
  border-radius: 12px;
  flex-shrink: 0;
  overflow: hidden;
`;

const StyledOverlay = styled(Flex)`
  position: absolute;
  inset: 0;
  background: rgba(0, 0, 0, 0.34);
  opacity: 0;
  transition: opacity 0.18s ease-in-out;
  border-radius: 12px;
  pointer-events: none;
`;

const StyledImg = styled.img`
  padding: 10px;
  width: 100%;
  height: 100%;
  object-fit: scale-down;
`;
