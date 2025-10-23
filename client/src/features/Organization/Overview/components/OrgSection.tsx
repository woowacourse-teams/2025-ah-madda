import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { OrganizationAPIResponse } from '@/api/types/organizations';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Spacing } from '@/shared/components/Spacing';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { OrgCard } from './OrgCard';

export type OrgSectionProps = {
  organizations: OrganizationAPIResponse[];
};
export const OrgSection = ({ organizations }: OrgSectionProps) => {
  const navigate = useNavigate();

  const handleJoin = (orgId: number) => navigate(`/${orgId}/event`);

  return (
    <Flex
      dir="column"
      gap="24px"
      margin="60px 0 0 0"
      padding="40px 20px 0 20px"
      css={css`
        @media (max-width: 768px) {
          padding: 20px 20px 0 20px;
          gap: 12px;
        }
      `}
    >
      <Flex
        dir="row"
        justifyContent="space-between"
        alignItems="flex-end"
        width="100%"
        margin="0 0 20px 0"
        css={css`
          @media (max-width: 768px) {
            flex-direction: column;
            align-items: flex-end;

            gap: 10px;
          }
        `}
      >
        <Flex dir="column" gap="8px" alignItems="flex-start" width="100%">
          <Text as="h1" type="Display" weight="bold">
            스페이스 목록 ({organizations.length})
          </Text>

          <Text as="h3" type="Body" color={theme.colors.gray500}>
            현재 가장 활발한 순서대로 스페이스를 노출해요.
          </Text>
        </Flex>
        {isAuthenticated() && (
          <Flex dir="column" gap="8px" alignItems="flex-end" width="100%">
            <Button
              size="md"
              color="primary"
              variant="solid"
              iconName="plus"
              onClick={() => navigate(`/organization/new`)}
            >
              스페이스 만들기
            </Button>
          </Flex>
        )}
      </Flex>

      <OrgListContainer dir="column" width="100%" gap="8px" margin="0 0 40px 0">
        <DeskTopOrgList>
          {organizations.map((org) => (
            <OrgCard
              key={org.organizationId}
              organization={org}
              onJoin={() => handleJoin(org.organizationId)}
            />
          ))}
        </DeskTopOrgList>

        <MobileOrgList dir="column">
          {organizations.map((org) => (
            <OrgCard
              key={org.organizationId}
              organization={org}
              onJoin={() => handleJoin(org.organizationId)}
            />
          ))}
        </MobileOrgList>
      </OrgListContainer>
      {organizations.length === 0 && (
        <Text type="Body" color={theme.colors.gray500}>
          아직 스페이스가 없어요. 새로운 스페이스를 만들어보세요!
        </Text>
      )}
    </Flex>
  );
};

const OrgListContainer = styled(Flex)`
  max-height: 50vh;
  overflow-y: auto;
`;

const DeskTopOrgList = styled.div`
  @media (min-width: 768px) {
    width: 100%;
    display: grid;
    grid-template-columns: repeat(auto-fill, minmax(280px, 3fr));
    flex-wrap: nowrap;
    overflow-x: auto;
    gap: 20px;
  }

  @media (max-width: 768px) {
    display: none;
  }
`;

const MobileOrgList = styled(Flex)`
  display: none;

  @media (max-width: 768px) {
    display: flex;
    gap: 8px;
    width: 100%;
    flex-shrink: 0;
    overflow-y: auto;
  }
`;
