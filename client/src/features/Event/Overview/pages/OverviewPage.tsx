import { Suspense } from 'react';

import { useQueryClient, useSuspenseQuery } from '@tanstack/react-query';
import { useParams } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { PageLayout } from '@/shared/components/PageLayout';

import { OrganizationInfo } from '../components/OrganizationInfo';
import { OrganizationInfoSkeleton, TabsSkeleton } from '../components/OverviewSkeletons';
import { OverviewTabs } from '../components/OverviewTabs';

// S.TODO : 추후 삭제
export const OverviewPage = () => {
  const { organizationId } = useParams();

  const orgIdNum = Number(organizationId);

  return (
    <>
      <PageLayout>
        <Suspense fallback={<OrganizationInfoSkeleton />}>
          <OrganizationInfoSection organizationId={organizationId!} />
        </Suspense>
        <Suspense fallback={<TabsSkeleton />}>
          <OverviewTabs organizationId={orgIdNum} />
        </Suspense>
      </PageLayout>
    </>
  );
};

const OrganizationInfoSection = ({ organizationId }: { organizationId: string }) => {
  const queryClient = useQueryClient();
  const { data: organizationData } = useSuspenseQuery({
    ...organizationQueryOptions.organizations(String(organizationId)),
    staleTime: 5 * 60 * 1000,
  });
  queryClient.prefetchQuery(organizationQueryOptions.profile(Number(organizationId)));

  return (
    <OrganizationInfo
      name={organizationData?.name ?? ''}
      description={organizationData?.description ?? ''}
      imageUrl={organizationData?.imageUrl ?? ''}
    />
  );
};
