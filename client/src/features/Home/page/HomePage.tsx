import { useEffect } from 'react';

import { css } from '@emotion/react';
import { useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { Icon } from '@/shared/components/Icon';
import { PageLayout } from '@/shared/components/PageLayout';
import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';
import { isIOS, isPWA } from '@/shared/utils/device';

import { useModal } from '../../../shared/hooks/useModal';
import { AlarmModal } from '../components/AlarmModal';
import { Description } from '../components/Description';
import { Info } from '../components/Info';

export const HomePage = () => {
  const navigate = useNavigate();
  const { logout } = useGoogleAuth();
  const { isOpen, open, close } = useModal();

  const handleGoogleLogin = () => {
    const authUrl = getGoogleAuthUrl();
    window.location.href = authUrl;
  };

  const shouldShowModal =
    isAuthenticated() && isIOS() && isPWA() && Notification.permission === 'default';

  useEffect(() => {
    if (shouldShowModal) {
      open();
    }
  }, [open, shouldShowModal]);

  return (
    <>
      <PageLayout
        header={
          <Header
            left={
              <Icon
                name="logo"
                size={55}
                onClick={() => navigate(`/`)}
                css={css`
                  cursor: pointer;
                `}
              />
            }
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
      <AlarmModal isOpen={isOpen} onClose={close} />
    </>
  );
};
