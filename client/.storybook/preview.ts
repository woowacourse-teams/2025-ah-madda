import type { Preview } from '@storybook/react-webpack5';

if (typeof window !== 'undefined') {
  (window as any).__STORYBOOK__ = true;
}

const preview: Preview = {
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/i,
      },
    },
  },
};

// eslint-disable-next-line import/no-default-export
export default preview;
