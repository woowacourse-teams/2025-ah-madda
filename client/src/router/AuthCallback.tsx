import { useEffect } from 'react';

import { useGoogleAuth } from '@/shared/hooks/useGoogleAuth';

export const AuthCallback = () => {
  const { handleCallback } = useGoogleAuth();

  useEffect(() => {
    handleCallback();
  }, []);

  return <div>AuthCallback</div>;
};
