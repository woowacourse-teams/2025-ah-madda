import { useEffect } from 'react';

import { useLocation } from 'react-router-dom';

import { pageview } from '../lib/gtag';

const isProd = process.env.NODE_ENV === 'production';

export const usePageTrack = () => {
  const location = useLocation();

  useEffect(() => {
    if (isProd) {
      pageview(location.pathname);
    }
  }, [location]);
};
