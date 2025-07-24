import { useEffect } from 'react';

import { useNavigate } from 'react-router-dom';

import { getAuthCodeFromUrl } from '@/api/auth';
import { useGoogleLoginMutation } from '@/api/authQueries';

export const AuthCallback = () => {
  const code = getAuthCodeFromUrl();
  const navigate = useNavigate();
  const { mutate: googleLogin } = useGoogleLoginMutation();

  useEffect(() => {
    if (!code) return;
    googleLogin(code, {
      onSuccess: () => {
        //E.TODO 로그인 성공하면 보낼 곳 수정
        navigate('/');
      },
    });
  }, [code, googleLogin, navigate]);

  return <></>;
};
