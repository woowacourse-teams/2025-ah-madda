import { useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';
import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';
import { theme } from '@/shared/styles/theme';

import { Flex } from '../../../shared/components/Flex/Flex';
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
              <Flex gap="8px">
                <Button
                  width="80px"
                  size="sm"
                  variant="outlined"
                  color={theme.colors.primary700}
                  fontColor={theme.colors.primary700}
                  onClick={logout}
                >
                  로그아웃
                </Button>
                <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
                  내 이벤트
                </Button>
              </Flex>
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
