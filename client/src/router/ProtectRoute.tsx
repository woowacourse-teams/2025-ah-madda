import { useEffect } from 'react';

import { Outlet, useLocation, useNavigate } from 'react-router-dom';

import { isAuthenticated } from '@/api/auth';
import { LoginModal } from '@/features/Event/Detail/components/LoginModal';
import { useToast } from '@/shared/components/Toast/ToastContext';
import { useModal } from '@/shared/hooks/useModal';

export const ProtectRoute = () => {
  const { error } = useToast();
  const location = useLocation();
  const navigate = useNavigate();

  const { isOpen, open, close } = useModal();

  useEffect(() => {
    if (!isAuthenticated()) {
      open();
      sessionStorage.setItem('redirectAfterLogin', location.pathname);
      error('로그인이 필요한 서비스입니다. 먼저 로그인해 주세요.', { duration: 1500 });
    } else {
      close();
    }
  }, [error, location.pathname, open, close]);

  if (!isAuthenticated()) {
    return <LoginModal isOpen={isOpen} onClose={() => navigate('/')} />;
  }

  return <Outlet />;
};
