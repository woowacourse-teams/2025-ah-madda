import { Suspense, useEffect, useState } from 'react';

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

import { OrgCard } from '../components/OrgCard';
import { OrganizationWithRole } from '../types/Organization';

const CARD_W = 120;
const GAP = 80;

function getVisibleCount(width: number) {
  if (width >= 1024) return 4;
  if (width >= 768) return 3;
  if (width >= 480) return 2;
  return 1;
}

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

  const { data: participatedOrgs } = useSuspenseQuery(organizationQueryOptions.participated());

  const baseOrgs: OrganizationWithRole[] = participatedOrgs.map((org) => ({
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

  const [visibleCount, setVisibleCount] = useState<number>(() =>
    typeof window !== 'undefined' ? getVisibleCount(window.innerWidth) : 4
  );
  useEffect(() => {
    const onResize = () => setVisibleCount(getVisibleCount(window.innerWidth));
    window.addEventListener('resize', onResize, { passive: true });
    return () => window.removeEventListener('resize', onResize);
  }, []);

  const maxRowWidth = () => CARD_W * visibleCount + GAP * Math.max(visibleCount - 1, 0);
  const justify = orgs.length <= visibleCount ? 'center' : 'flex-start';

  const handleJoin = (orgId: number) => navigate(`/event?organizationId=${orgId}`);
  const handleEdit = (orgId: number) => navigate(`/organization/edit/${orgId}`);

  return (
    <Flex
      dir="column"
      width="100%"
      css={css`
        flex: 1;
        margin-top: 60px;
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
            max-width: ${maxRowWidth()}px;
            overflow-x: auto;
            flex-wrap: nowrap;
            &::-webkit-scrollbar {
              height: 8px;
            }
            &::-webkit-scrollbar-thumb {
              border-radius: 8px;
              background: #d1d5db;
            }
          `}
          justifyContent={justify}
        >
          {orgs.map((org) => (
            <Flex key={org.organizationId}>
              <OrgCard
                org={org}
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
