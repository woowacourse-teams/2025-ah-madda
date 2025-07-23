import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { data: eventData } = useQuery(organizationQueryOptions.event(1));
  const { data: organizationData } = useQuery(
    organizationQueryOptions.organizations('woowacourse')
  );

  // S.TODO: 로딩 상태 처리
  if (!eventData || !organizationData) {
    return <div>Loading...</div>;
  }

  return (
    <>
      <Header
        left="아맞다"
        right={
          <Flex gap="8px">
            <Button width="80px" size="sm" variant="outlined" fontColor="#2563EB">
              로그아웃
            </Button>
            <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          </Flex>
        }
      />

      <OrganizationInfo
        name={organizationData?.name}
        description={organizationData?.description}
        imageUrl={organizationData?.imageUrl}
        totalEvents={eventData?.length}
      />
      <EventList events={eventData ?? []} />
    </>
  );
};
