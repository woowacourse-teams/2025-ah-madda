import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { HomePage } from '@/features/Home/page/HomePage';

describe('RTL Test', () => {
  it('should render', () => {
    const queryClient = new QueryClient({
      defaultOptions: { queries: { retry: false } },
    });
    render(
      <MemoryRouter>
        <QueryClientProvider client={queryClient}>
          <HomePage />
        </QueryClientProvider>
      </MemoryRouter>
    );

    expect(screen.getByText('채널이 너무 많아 정보가 분산되어 알 수 없었다.')).toBeInTheDocument();
  });
});
