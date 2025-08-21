import React from 'react';

import { ThemeProvider } from '@emotion/react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Routes, Route } from 'react-router-dom';

import { ToastProvider } from '@/shared/components/Toast/ToastContext';

import { theme } from '../shared/styles/theme';

import { createTestQueryClient } from './createTestQueryClient';

export const QueryClientProviderWrapper = ({
  children,
  queryClient,
}: {
  children: React.ReactNode;
  queryClient: QueryClient;
}) => {
  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
};

export const RouterWithQueryClient = ({
  initialRoute,
  routes,
}: {
  initialRoute: string;
  routes: Array<{ path: string; element: React.ReactNode }>;
}) => {
  const queryClient = createTestQueryClient();

  return (
    <ThemeProvider theme={theme}>
      <QueryClientProviderWrapper queryClient={queryClient}>
        <MemoryRouter initialEntries={[initialRoute]}>
          <ToastProvider>
            <Routes>
              {routes.map((route, index) => (
                <Route key={index} path={route.path} element={route.element} />
              ))}
            </Routes>
          </ToastProvider>
        </MemoryRouter>
      </QueryClientProviderWrapper>
    </ThemeProvider>
  );
};

export const ThemeProviderWrapper = ({ children }: { children: React.ReactNode }) => {
  return <ThemeProvider theme={theme}>{children}</ThemeProvider>;
};
