import React from 'react';

import { ThemeProvider } from '@emotion/react';
import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';

import './reset.css';

import { ClientQueryProvider } from './api/ClientQueryProvider';
import { router } from './router/route';
import { theme } from './shared/styles/theme';

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <ClientQueryProvider>
      <ThemeProvider theme={theme}>
        <RouterProvider router={router} />
      </ThemeProvider>
    </ClientQueryProvider>
  </React.StrictMode>
);
