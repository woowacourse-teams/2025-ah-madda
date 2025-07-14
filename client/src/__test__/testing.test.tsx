import { render, screen } from '@testing-library/react';

import { App } from '@/App';

describe('RTL Test', () => {
  it('should render', () => {
    render(<App />);
    expect(screen.getByText('React + TypeScript + Webpack')).toBeInTheDocument();
  });
});
