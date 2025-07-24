import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';

import { HomePage } from '@/features/Home/page/HomePage';

describe('RTL Test', () => {
  it('should render', () => {
    render(
      <MemoryRouter>
        <HomePage />
      </MemoryRouter>
    );
    screen.debug();

    expect(
      screen.getByText('슬랙이나 메신저에서 참여하고 싶었던 이벤트를 놓친 경험 있으신가요?')
    ).toBeInTheDocument();
  });
});
