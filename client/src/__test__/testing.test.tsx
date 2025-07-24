import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { HomePage } from '../features/Home/page/HomePage';

describe('RTL Test', () => {
  it('should render', () => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: { retry: false },
        mutations: { retry: false },
      },
    });

    render(
      <QueryClientProvider client={queryClient}>
        <MemoryRouter>
          <HomePage />
        </MemoryRouter>
      </QueryClientProvider>
    );

    screen.debug();

    expect(
      screen.getByText('슬랙이나 메신저에서 참여하고 싶었던 이벤트를 놓친 경험 있으신가요?')
    ).toBeInTheDocument();
  });
});
