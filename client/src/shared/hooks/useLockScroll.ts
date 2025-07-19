import { useEffect } from 'react';

export const useLockScroll = () => {
  useEffect(() => {
    const isInIframe = window.self !== window.top;
    const originalOverflow = document.body.style.overflow;

    if (!isInIframe) {
      document.body.style.overflow = 'hidden';
    }

    return () => {
      document.body.style.overflow = originalOverflow;
    };
  }, []);
};
