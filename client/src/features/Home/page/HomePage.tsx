import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';

import { Description } from '../component/Description';
import { Info } from '../component/Info';

export const HomePage = () => {
  const { logout } = useGoogleAuth();
  const handleGoogleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };
  return (
    <PageLayout
      header={
        <Header
          left={<Icon name="logo" width={55} />}
          right={
            isAuthenticated() ? (
              <Button width="80px" size="sm" onClick={logout}>
                로그아웃
              </Button>
            ) : (
              <Button width="80px" size="sm" onClick={handleGoogleLogin}>
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
