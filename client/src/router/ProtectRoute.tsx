import { useEffect } from 'react';

import { Outlet, useLocation } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { setLocalStorage } from '@/shared/utils/localStorage';

export const ProtectRoute = () => {
  const { error } = useToast();
  const location = useLocation();

  useEffect(() => {
    if (!isAuthenticated()) {
      setLocalStorage('redirectAfterLogin', location.pathname);
      error('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.', { duration: 1500 });
      const authUrl = getGoogleAuthUrl();
      setTimeout(() => {
        window.location.href = authUrl;
      }, 1500);
    }
  }, [error, location.pathname]);

  if (!isAuthenticated()) {
    return null;
  }

  return <Outlet />;
};
