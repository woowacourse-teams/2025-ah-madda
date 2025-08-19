import React from 'react';

import { ThemeProvider } from '@emotion/react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';

import './reset.css';
import packageJson from '../package.json';

import { ClientQueryProvider } from './api/ClientQueryProvider';
import { initSentry } from './lib/sentry';
import { router } from './router/route';
import { theme } from './shared/styles/theme';

// Version information for developer tools
const APP_VERSION = packageJson.version;
const APP_BUILD_TIME = new Date().toISOString();

// Console logging for version info
console.info(`🚀 AhMadda v${APP_VERSION} loaded successfully!`);
console.info(`📅 Build time: ${APP_BUILD_TIME}`);
console.info(`🏷️ Git tag: v${APP_VERSION}`);

// Global variables for easy access in developer tools
(window as any).APP_VERSION = APP_VERSION;
(window as any).APP_BUILD_TIME = APP_BUILD_TIME;

initSentry();

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
