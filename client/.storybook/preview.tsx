import { ThemeProvider } from '@emotion/react';
import type { Preview } from '@storybook/react-webpack5';

import { theme } from '../src/shared/styles/theme';

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
  decorators: [
    (Story) => (
      <ThemeProvider theme={theme}>
        <Story />
      </ThemeProvider>
    ),
  ],
};

// eslint-disable-next-line import/no-default-export
export default preview;
