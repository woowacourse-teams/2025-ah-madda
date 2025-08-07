import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();

  const { data: organizationData } = useQuery(
    organizationQueryOptions.organizations('woowacourse')
  );

  const { data: eventData } = useQuery(organizationQueryOptions.event(1));

  // S.TODO 로딩 처리

  return (
    <PageLayout
      header={
        <Header
          left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
          right={
            <Button size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <OrganizationInfo
        name={organizationData?.name ?? ''}
        description={organizationData?.description ?? ''}
        imageUrl={organizationData?.imageUrl ?? ''}
      />

      <EventList organizationId={organizationData?.organizationId ?? 0} events={eventData ?? []} />
    </PageLayout>
  );
};
