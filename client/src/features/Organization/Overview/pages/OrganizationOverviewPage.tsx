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
      <Flex dir="column" width="100%" margin="0 auto" padding="28px 20px" gap="24px">
        <OrgSection organizations={organizations} />
      </Flex>
    </PageLayout>
  );
};
