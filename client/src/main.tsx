import React from 'react';

import ReactDOM from 'react-dom/client';
import ReactGA from 'react-ga4';
import { RouterProvider } from 'react-router-dom';

import './reset.css';

import { ClientQueryProvider } from './api/ClientQueryProvider';
import { router } from './router/route';

if (process.env.GOOGLE_ANALYTICS_ID) {
  ReactGA.initialize(process.env.GOOGLE_ANALYTICS_ID);
}

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ClientQueryProvider>
      <RouterProvider router={router} />
    </ClientQueryProvider>
  </React.StrictMode>
);
