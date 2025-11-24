import { Flex } from '@/shared/components/Flex';
import { PageLayout } from '@/shared/components/PageLayout';

import { OrganizationCreateForm } from '../components/OrganizationCreateForm';

export const NewOrganizationPage = () => {
  return (
    <PageLayout>
      <Flex dir="column" width="100%" margin="0 auto" padding="28px 20px" gap="24px">
        <OrganizationCreateForm />
      </Flex>
    </PageLayout>
  );
};
