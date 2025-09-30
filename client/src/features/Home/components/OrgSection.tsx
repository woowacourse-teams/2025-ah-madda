import { css } from '@emotion/react';
import styled from '@emotion/styled';
import { useSuspenseQueries, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import type { OrganizationWithRole } from '../../Organization/types/Organization';

import { OrgCard } from './OrgCard';

const GAP = 80;
const MAX_W_4 = 800;

export const OrgSection = () => {
  const navigate = useNavigate();

  const { data: joinedOrgs } = useSuspenseQuery(organizationQueryOptions.joined());
  const profileQueries = useSuspenseQueries({
    queries: joinedOrgs.map((org) => organizationQueryOptions.profile(org.organizationId)),
  });

  const orgs: OrganizationWithRole[] = joinedOrgs.map((org, idx) => ({
    ...org,
    isAdmin: profileQueries[idx]?.data?.isAdmin ?? false,
  }));

  const handleJoin = (orgId: number) => navigate(`/${orgId}/event`);
  const handleEdit = (orgId: number) => navigate(`/organization/edit/${orgId}`);

  return (
    <Flex
      dir="column"
      width="100%"
      justifyContent="center"
      alignItems="center"
      gap="32px"
      padding="40px 20px"
    >
      <Flex width="100%" justifyContent="center">
        <DeskTopOrgList dir="row" margin="0 auto" padding="8px 4px" gap={`${GAP}px`}>
          {orgs.map((org) => (
            <Flex key={org.organizationId}>
              <OrgCard
                organization={org}
                onJoin={() => handleJoin(org.organizationId)}
                isAdmin={org.isAdmin}
                onEdit={() => handleEdit(org.organizationId)}
              />
            </Flex>
          ))}
        </DeskTopOrgList>

        <MobileOrgList dir="column">
          {orgs.map((org) => (
            <Flex
              key={org.organizationId}
              onClick={() => handleJoin(org.organizationId)}
              alignItems="center"
              justifyContent="space-between"
              width="100%"
              gap="12px"
              padding="10px 12px"
              css={css`
                border: 1px solid ${theme.colors.gray200};
                border-radius: 12px;
                cursor: pointer;

                &:hover {
                  background: ${theme.colors.gray50};
                }
              `}
            >
              <Flex
                as="span"
                width="56px"
                height="56px"
                css={css`
                  border-radius: 10px;
                  background: ${theme.colors.gray100};
                  background-image: ${org.imageUrl ? `url(${org.imageUrl})` : 'none'};
                  background-size: cover;
                  background-position: center;
                `}
              />

              <Text
                type="Body"
                weight="bold"
                css={css`
                  flex: 1;
                  overflow: hidden;
                  text-overflow: ellipsis;
                `}
              >
                {org.name}
              </Text>

              {org.isAdmin && (
                <Button
                  size="sm"
                  color="secondary"
                  onClick={(e) => {
                    e.stopPropagation();
                    handleEdit(org.organizationId);
                  }}
                >
                  수정
                </Button>
              )}
            </Flex>
          ))}
        </MobileOrgList>
      </Flex>

      {orgs.length === 0 && (
        <Text type="Body" color="#6b7280">
          아직 소속된 이벤트 스페이스가 없어요. 새로운 이벤트 스페이스를 만들어보세요!
        </Text>
      )}
    </Flex>
  );
};

const DeskTopOrgList = styled(Flex)`
  width: fit-content;
  max-width: ${MAX_W_4}px;

  @media (min-width: 768px) {
    overflow-x: auto;
    flex-wrap: nowrap;

    &::-webkit-scrollbar {
      height: 8px;
    }
    &::-webkit-scrollbar-thumb {
      border-radius: 8px;
      background: ${theme.colors.gray300};
    }
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
    overflow-y: auto;
    padding: 0 10px;

    &::-webkit-scrollbar {
      width: 8px;
    }

    &::-webkit-scrollbar-thumb {
      border-radius: 8px;
      background: ${theme.colors.gray300};
    }
  }
`;
