import { css } from '@emotion/react';
import { useSuspenseQueries } from '@tanstack/react-query';
import { useNavigate, useParams } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { organizationId } = useParams();

  const [{ data: organizationData }, { data: eventData }] = useSuspenseQueries({
    queries: [
      organizationQueryOptions.organizations(String(organizationId)),
      organizationQueryOptions.event(Number(organizationId)),
    ],
  });

  const goMyEvents = () => navigate(`/${organizationId}/event/my`);
  const goHome = () => navigate(`/${organizationId}/event`);

  // S.TODO 로딩 처리

  return (
    <PageLayout
      header={
        <Header
          left={
            <Icon
              name="logo"
              size={55}
              onClick={goHome}
              css={css`
                cursor: pointer;
              `}
            />
          }
          right={
            <Button size="sm" onClick={goMyEvents}>
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
