import React from 'react';

import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { MemoryRouter, Routes, Route } from 'react-router-dom';

const createTestQueryClient = () => {
  return new QueryClient({
    defaultOptions: {
      queries: { retry: false, staleTime: 0 },
      mutations: { retry: false },
    },
  });
};

const TestWrapper = ({
  children,
  queryClient,
}: {
  children: React.ReactNode;
  queryClient: QueryClient;
}) => {
  return <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>;
};

TestWrapper.displayName = 'TestWrapper';

export const TestContainer = ({
  initialRoute,
  routes,
}: {
  initialRoute: string;
  routes: Array<{ path: string; element: React.ReactNode }>;
}) => {
  const queryClient = createTestQueryClient();

  return (
    <TestWrapper queryClient={queryClient}>
      <MemoryRouter initialEntries={[initialRoute]}>
        <Routes>
          {routes.map((route, index) => (
            <Route key={index} path={route.path} element={route.element} />
          ))}
        </Routes>
      </MemoryRouter>
    </TestWrapper>
  );
};

export const HookTestContainer = ({ children }: { children: React.ReactNode }) => {
  const queryClient = createTestQueryClient();

  return <TestWrapper queryClient={queryClient}>{children}</TestWrapper>;
};
