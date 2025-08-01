import React from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Routes, Route } from 'react-router-dom';

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
    <QueryClientProviderWrapper queryClient={queryClient}>
      <MemoryRouter initialEntries={[initialRoute]}>
        <Routes>
          {routes.map((route, index) => (
            <Route key={index} path={route.path} element={route.element} />
          ))}
        </Routes>
      </MemoryRouter>
    </QueryClientProviderWrapper>
  );
};
