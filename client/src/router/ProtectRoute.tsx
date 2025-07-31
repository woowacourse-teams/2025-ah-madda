import { useEffect } from 'react';

import { Outlet, useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';

export const ProtectRoute = () => {
  const navigate = useNavigate();

  useEffect(() => {
    if (!isAuthenticated()) {
      alert('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.');
      navigate('/');
    }
  }, [navigate]);

  if (!isAuthenticated()) {
    return null;
  }

  return <Outlet />;
};
