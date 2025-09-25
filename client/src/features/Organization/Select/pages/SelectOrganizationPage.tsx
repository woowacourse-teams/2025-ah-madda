import { css } from '@emotion/react';
import { useSuspenseQueries, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import type { OrganizationWithRole } from '../../types/Organization';
import { OrgCard } from '../components/OrgCard';

const CARD_W = 120;
const GAP = 80;

const MAX_W_4 = CARD_W * 4 + GAP * 3;
const MAX_W_3 = CARD_W * 3 + GAP * 2;
const MAX_W_2 = CARD_W * 2 + GAP * 1;
const MOBILE_ROW_HEIGHT = 250;

export const OrganizationSelectPage = () => {
  const navigate = useNavigate();
  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              onClick={() => navigate(`/`)}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Button
              color="secondary"
              css={css`
                width: fit-content;
                padding: 10px;
              `}
              onClick={() => navigate('/organization/new')}
            >
              이벤트 스페이스 생성하기
            </Button>
          }
        />
      }
    >
      <OrganizationSelectBody />
    </PageLayout>
  );
};

const OrganizationSelectBody = () => {
  const navigate = useNavigate();

  const { data: joinedOrgs } = useSuspenseQuery(organizationQueryOptions.joined());

  const baseOrgs: OrganizationWithRole[] = joinedOrgs.map((org) => ({
    organizationId: org.organizationId,
    name: org.name,
    description: org.description,
    imageUrl: org.imageUrl,
    isAdmin: false,
  }));

  const profileQueries = useSuspenseQueries({
    queries: baseOrgs.map((org) => organizationQueryOptions.profile(org.organizationId)),
  });

  const orgs: OrganizationWithRole[] = baseOrgs.map((org, idx) => ({
    ...org,
    isAdmin: profileQueries[idx]?.data?.isAdmin ?? org.isAdmin,
  }));

  const handleJoin = (orgId: number) => navigate(`/${orgId}/event`);
  const handleEdit = (orgId: number) => navigate(`/organization/edit/${orgId}`);

  return (
    <Flex
      dir="column"
      width="100%"
      css={css`
        flex: 1;
      `}
      justifyContent="center"
      alignItems="center"
      gap="32px"
      padding="28px 20px"
    >
      <Flex dir="column" alignItems="center" width="100%" gap="4px" margin="0 auto">
        <Text
          as="h1"
          type="Display"
          weight="bold"
          css={css`
            @media (max-width: 720px) {
              font-size: 1.7rem;
            }
          `}
        >
          이벤트 스페이스에 참여하고,
        </Text>
        <Text
          as="h1"
          type="Display"
          weight="bold"
          css={css`
            @media (max-width: 720px) {
              font-size: 1.7rem;
            }
          `}
        >
          이벤트를 놓치지 마세요.
        </Text>
      </Flex>

      <Flex width="100%" justifyContent="center">
        <Flex
          dir="row"
          margin="0 auto"
          padding="8px 4px"
          gap={`${GAP}px`}
          css={css`
            width: fit-content;

            max-width: ${MAX_W_4}px;
            @media (max-width: 1024px) {
              max-width: ${MAX_W_3}px;
            }
            @media (max-width: 768px) {
              max-width: ${MAX_W_2}px;
            }

            @media (min-width: 481px) {
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

            @media (max-width: 480px) {
              display: none;
            }
          `}
        >
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
        </Flex>

        <Flex
          dir="column"
          css={css`
            display: none;

            @media (max-width: 480px) {
              display: flex;
              gap: 8px;
              width: 100%;
              max-width: 100%;
              overflow-y: auto;
              max-height: ${MOBILE_ROW_HEIGHT}px;
              padding: 0 8px;
              margin: 0 auto;
            }
          `}
        >
          {orgs.map((org) => (
            <Flex
              key={org.organizationId}
              onClick={() => handleJoin(org.organizationId)}
              alignItems="center"
              width="100%"
              gap="12px"
              padding="10px 12px"
              css={css`
                border: 1px solid ${theme.colors.gray200};
                border-radius: 12px;
                background: #fff;
                text-align: left;
                cursor: pointer;
                transition: background 0.15s ease;

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
                  white-space: nowrap;
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
        </Flex>
      </Flex>

      {orgs.length === 0 && (
        <Text type="Body" color="#6b7280">
          아직 소속된 이벤트 스페이스가 없어요. 새로운 이벤트 스페이스를 만들어보세요!
        </Text>
      )}
    </Flex>
  );
};
