import { useEffect } from 'react';

export const useLockScroll = (shouldLock: boolean) => {
  useEffect(() => {
    if (!shouldLock) return;

    document.body.style.overflow = 'hidden';
    return () => {
      document.body.style.overflow = '';
    };
  }, [shouldLock]);
};
