import { useEffect } from 'react';

import { Outlet, useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';

import { useToast } from '../shared/components/Toast/ToastContext';

export const ProtectRoute = () => {
  const navigate = useNavigate();
  const { error } = useToast();

  useEffect(() => {
    if (!isAuthenticated()) {
      error('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.', { duration: 3000 });
      navigate('/');
    }
  }, [navigate, error]);

  if (!isAuthenticated()) {
    return null;
  }

  return <Outlet />;
};
