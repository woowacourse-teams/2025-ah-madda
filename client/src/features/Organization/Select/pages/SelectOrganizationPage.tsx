import { Suspense } from 'react';

import { css } from '@emotion/react';
import { useSuspenseQueries, useSuspenseQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { ErrorPage } from '@/features/Error/pages/ErrorPage';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { Text } from '@/shared/components/Text';
import { theme } from '@/shared/styles/theme';

import { OrganizationWithRole } from '../../types/Organization';
import { OrgCard } from '../components/OrgCard';

const CARD_W = 120;
const GAP = 80;

const MAX_W_4 = CARD_W * 4 + GAP * 3;
const MAX_W_3 = CARD_W * 3 + GAP * 2;
const MAX_W_2 = CARD_W * 2 + GAP * 1;
const MOBILE_ROW_HEIGHT = 168;

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
              onClick={() => navigate('/event')}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Button color="secondary" onClick={() => navigate('/organization/new')}>
              조직 생성하기
            </Button>
          }
        />
      }
    >
      <Suspense fallback={<ErrorPage />}>
        <OrganizationSelectBody />
      </Suspense>
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

  const handleJoin = (orgId: number) => navigate(`/event?organizationId=${orgId}`);
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
      <Flex
        dir="column"
        alignItems="center"
        gap="4px"
        css={css`
          width: 100%;
          @media (max-width: 720px) {
            width: 312px;
            margin-left: auto;
            margin-right: auto;
          }
        `}
      >
        <Text as="h1" type="Display" weight="bold">
          조직에 참여하고,
        </Text>
        <Text as="h1" type="Display" weight="bold">
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
              display: grid;
              grid-template-columns: repeat(2, ${CARD_W}px);
              justify-content: center;
              column-gap: 20px;
              row-gap: 16px;

              width: 100%;
              max-width: 100%;
              overflow-x: hidden;
              overflow-y: auto;
              max-height: ${MOBILE_ROW_HEIGHT}px;
              padding-right: 8px;
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
      </Flex>

      {orgs.length === 0 && (
        <Text type="Body" color="#6b7280">
          아직 소속된 조직이 없어요. 새로운 조직을 만들어보세요!
        </Text>
      )}
    </Flex>
  );
};
