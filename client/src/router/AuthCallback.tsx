import { useEffect } from 'react';

import { useNavigate } from 'react-router-dom';

import { getAuthCodeFromUrl } from '@/api/auth';
import { useGoogleLoginMutation } from '@/api/authQueries';
import { getLocalStorage, removeLocalStorage } from '@/shared/utils/localStorage';

export const AuthCallback = () => {
  const code = getAuthCodeFromUrl();
  const navigate = useNavigate();
  const { mutate: googleLogin } = useGoogleLoginMutation();

  const inviteCode = getLocalStorage('inviteCode');
  useEffect(() => {
    if (!code) return;
    googleLogin(code, {
      onSuccess: () => {
        if (inviteCode) {
          navigate(`/invite?code=${inviteCode}`);
        } else {
          const redirectAfterLogin = getLocalStorage('redirectAfterLogin');
          if (redirectAfterLogin) {
            removeLocalStorage('redirectAfterLogin');
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
