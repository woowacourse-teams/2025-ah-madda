import { useEffect } from 'react';

import { useNavigate } from 'react-router-dom';

import { getAuthCodeFromUrl } from '@/api/auth';
import { useGoogleLoginMutation } from '@/api/authQueries';
import { getLocalStorage } from '@/shared/utils/localStorage';

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
          navigate('/');
        }
      },
    });
  }, [code, googleLogin, navigate, inviteCode]);

  return <></>;
};
