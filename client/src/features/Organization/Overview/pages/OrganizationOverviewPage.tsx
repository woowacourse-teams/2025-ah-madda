import { useSuspenseQuery } from '@tanstack/react-query';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Flex } from '@/shared/components/Flex';
import { PageLayout } from '@/shared/components/PageLayout';

import { OrgSection } from '../components/OrgSection';

export const OrganizationOverviewPage = () => {
  const { data: organizations } = useSuspenseQuery({
    ...organizationQueryOptions.all(),
    staleTime: 5 * 60 * 1000,
  });

  return (
    <PageLayout>
      <Flex dir="column" gap="24px" padding="20px 0 0 0">
        <OrgSection organizations={organizations} />
      </Flex>
    </PageLayout>
  );
};
