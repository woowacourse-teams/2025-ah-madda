import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';
import { theme } from '@/shared/styles/theme';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { logout } = useGoogleAuth();

  const { data: organizationData } = useQuery(
    organizationQueryOptions.organizations('woowacourse')
  );

  const { data: eventData } = useQuery(organizationQueryOptions.event(1));

  // S.TODO 로딩 처리
  if (!organizationData || !eventData) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Header
        left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
        right={
          <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
            내 이벤트
          </Button>
        }
      />
      <OrganizationInfo
        name={organizationData?.name}
        description={organizationData?.description}
        imageUrl={organizationData?.imageUrl}
        totalEvents={eventData?.length}
      />

      <EventList events={eventData} />
    </>
  );
};
