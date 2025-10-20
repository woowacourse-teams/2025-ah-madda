import styled from '@emotion/styled';

import { OrganizationAPIResponse } from '@/api/types/organizations';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

export type OrgCardProps = {
  organization: OrganizationAPIResponse;
  onJoin: () => void;
};

export const OrgCard = ({ organization, onJoin }: OrgCardProps) => (
  <StyledCardContainer
    dir="row"
    alignItems="center"
    role="button"
    width="100%"
    gap="24px"
    aria-label={`${organization.name} 참여하기`}
    onClick={onJoin}
  >
    <StyledImageWrapper justifyContent="center" alignItems="center" width="155px" height="155px">
      <StyledImg
        src={organization.imageUrl}
        alt={organization.name}
        onError={(e) => {
          e.currentTarget.src = '/icon-192x192.png';
        }}
      />
    </StyledImageWrapper>
    <Flex dir="column" gap="4px" justifyContent="center" alignItems="flex-start" width="100%">
      <Text type="Title" weight="bold">
        {organization.name}
      </Text>
      <Text type="Body" color={theme.colors.gray500}>
        {organization.description}
      </Text>
    </Flex>
    <StyledOverlay data-overlay justifyContent="center" alignItems="center">
      <Text type="Heading" color="white" weight="semibold">
        구경하기
      </Text>
    </StyledOverlay>
  </StyledCardContainer>
);

const StyledCardContainer = styled(Flex)`
  cursor: pointer;
  position: relative;
  border-radius: 12px;

  &:hover [data-overlay] {
    opacity: 1;
  }
`;

const StyledImageWrapper = styled(Flex)`
  border-radius: 12px;
  border: 1px solid ${theme.colors.gray100};
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
  width: 100%;
  height: 100%;
  object-fit: scale-down;
`;
