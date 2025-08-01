import { useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';

import { Description } from '../component/Description';
import { Info } from '../component/Info';

export const HomePage = () => {
  const navigate = useNavigate();
  const { logout } = useGoogleAuth();

  const handleGoogleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };

  return (
    <PageLayout
      header={
        <Header
          left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
          right={
            isAuthenticated() ? (
              <Button size="sm" onClick={logout}>
                로그아웃
              </Button>
            ) : (
              <Button size="sm" onClick={handleGoogleLogin}>
                로그인
              </Button>
            )
          }
        />
      }
    >
      <Info />
      <Description />
    </PageLayout>
  );
};
