import { useEffect } from 'react';

import { Outlet, useLocation } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { useToast } from '@/shared/components/Toast/ToastContext';

export const ProtectRoute = () => {
  const { error } = useToast();
  const location = useLocation();

  useEffect(() => {
    if (!isAuthenticated()) {
      sessionStorage.setItem('redirectAfterLogin', location.pathname);
      error('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.', { duration: 1500 });
      const authUrl = getGoogleAuthUrl();
      window.location.href = authUrl;
    }
  }, [error, location.pathname]);

  if (!isAuthenticated()) {
    return null;
  }

  return <Outlet />;
};
