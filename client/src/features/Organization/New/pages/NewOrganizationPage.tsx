import { useNavigate, useParams } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';

import { OrganizationCreateForm } from '../components/OrganizationCreateForm';

export const NewOrganizationPage = () => {
  const navigate = useNavigate();
  const { organizationId } = useParams();
  return (
    <PageLayout
      header={
        <Header
          left={
            <IconButton
              name="logo"
              size={55}
              onClick={() => navigate(`/${organizationId}/event`)}
            />
          }
          right={
            <Button size="sm" onClick={() => navigate(`/${organizationId}/event/my`)}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <Flex dir="column" width="100%" margin="0 auto" padding="28px 20px" gap="24px">
        <OrganizationCreateForm />
      </Flex>
    </PageLayout>
  );
};
