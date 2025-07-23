import { useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl } from '@/api/auth';
import { useGoogleAuth } from '@/hooks/useGoogleAuth';
import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';

import { EventList } from '../components/EventList';
import { OrganizationInfo } from '../components/OrganizationInfo';

export const OverviewPage = () => {
  const navigate = useNavigate();
  const { logout, isAuthenticated } = useGoogleAuth();

  const handleGoogleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };

  return (
    <>
      <Header
        left="아맞다"
        right={
          <Flex gap="8px">
            {isAuthenticated ? (
              <Button
                width="80px"
                size="sm"
                variant="outlined"
                fontColor="#2563EB"
                onClick={logout}
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

      <OrganizationInfo />
      <EventList />
    </>
  );
};
