import { useEffect } from 'react';

import { Outlet, useLocation, useNavigate } from 'react-router-dom';

import { getGoogleAuthUrl, isAuthenticated } from '@/api/auth';
import { setLocalStorage } from '@/shared/utils/localStorage';

export const ProtectRoute = () => {
  const navigate = useNavigate();
  const location = useLocation();

  useEffect(() => {
    if (!isAuthenticated()) {
      setLocalStorage('redirectAfterLogin', location.pathname);
      alert('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.');
      const authUrl = getGoogleAuthUrl();
      window.location.href = authUrl;
    }
  }, [navigate, location.pathname]);

  if (!isAuthenticated()) {
    return null;
  }

  return <Outlet />;
};
