import { useNavigate } from 'react-router-dom';

import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';

import { OrganizationCreateForm } from '../components/OrganizationCreateForm';

export const NewOrganizationPage = () => {
  const navigate = useNavigate();
  return (
    <PageLayout
      header={<Header left={<IconButton name="logo" size={55} onClick={() => navigate(`/`)} />} />}
    >
      <Flex dir="column" width="100%" margin="0 auto" padding="28px 20px" gap="24px">
        <OrganizationCreateForm />
      </Flex>
    </PageLayout>
  );
};
