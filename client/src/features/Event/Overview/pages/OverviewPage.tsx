import { useQuery } from '@tanstack/react-query';
import { useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { organizationQueryOptions } from '@/api/queries/organization';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { data: eventData } = useQuery(organizationQueryOptions.event(1));
  const { data: organizationData } = useQuery(
    organizationQueryOptions.organizations('woowacourse')
  );
  const { logout } = useGoogleAuth();

  // S.TODO: 로딩 상태 처리
  if (!eventData || !organizationData) {
    return <div>Loading...</div>;
  }

  const handleGoogleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };

  return (
    <>
      <Header
        left={<Icon name="logo" width={55} />}
        right={
          <Flex gap="8px">
            {isAuthenticated() ? (
              <Button
                width="80px"
                size="sm"
                variant="outlined"
                onClick={logout}
                color="#18A0FB"
                fontColor="#18A0FB"
              >
                로그아웃
              </Button>
            ) : (
              <Button
                width="80px"
                size="sm"
                variant="filled"
                fontColor="#FFF"
                onClick={handleGoogleLogin}
              >
                로그인
              </Button>
            )}
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
