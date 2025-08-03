import React from 'react';

import { ThemeProvider } from '@emotion/react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';

import './reset.css';
import { ClientQueryProvider } from './api/ClientQueryProvider';
import { router } from './router/route';
import { theme } from './shared/styles/theme';

const GA_ID = process.env.GOOGLE_ANALYTICS_ID;

if (GA_ID) {
  const script = document.createElement('script');
  script.src = `https://www.googletagmanager.com/gtag/js?id=${GA_ID}`;
  script.async = true;
  document.head.appendChild(script);

  window.dataLayer = window.dataLayer || [];
  window.gtag = function () {
    // eslint-disable-next-line prefer-rest-params
    window.dataLayer.push(arguments);
  };

  window.gtag('js', new Date());
  window.gtag('config', GA_ID);
} else {
  console.warn('❌ GOOGLE_ANALYTICS_ID를 찾을 수 없습니다.');
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ClientQueryProvider>
      <ThemeProvider theme={theme}>
        <RouterProvider router={router} />
      </ThemeProvider>
    </ClientQueryProvider>
  </React.StrictMode>
);
