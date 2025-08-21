import { useEffect } from 'react';

import { useNavigate } from 'react-router-dom';

import { getAuthCodeFromUrl } from '@/api/auth';
import { useGoogleLoginMutation } from '@/api/authQueries';

export const AuthCallback = () => {
  const code = getAuthCodeFromUrl();
  const navigate = useNavigate();
  const { mutate: googleLogin } = useGoogleLoginMutation();

  const inviteCode = sessionStorage.getItem('inviteCode');
  useEffect(() => {
    if (!code) return;
    googleLogin(code, {
      onSuccess: () => {
        if (inviteCode) {
          navigate(`/invite?code=${inviteCode}`);
        } else {
          const redirectAfterLogin = sessionStorage.getItem('redirectAfterLogin');
          if (redirectAfterLogin) {
            sessionStorage.removeItem('redirectAfterLogin');
            navigate(redirectAfterLogin);
          } else {
            navigate('/');
          }
        }
      },
    });
  }, [code, googleLogin, navigate, inviteCode]);

  return <></>;
};
