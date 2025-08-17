import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';
import { useNavigate, useSearchParams } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const [searchParams] = useSearchParams();

  const orgIdParam = searchParams.get('organizationId');
  const organizationId = orgIdParam ? Number(orgIdParam) : undefined;

  const { data: organizationData } = useQuery({
    ...organizationQueryOptions.organizations(String(organizationId ?? '')),
    enabled: !!organizationId,
  });

  const { data: eventData } = useQuery({
    ...organizationQueryOptions.event(organizationId as number),
    enabled: !!organizationId,
  });

  const goMyEvents = () => navigate(`/event/my?organizationId=${organizationId ?? ''}`);
  const goHome = () => navigate(`/event?organizationId=${organizationId ?? ''}`);

  // S.TODO 로딩 처리

  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              onClick={goMyEvents}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Button size="sm" onClick={goHome}>
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
