import * as path from 'path';
import { fileURLToPath } from 'url';

import type { StorybookConfig } from '@storybook/react-webpack5';

const __filename = fileURLToPath(import.meta.url);
const __dirname = path.dirname(__filename);

export const config: StorybookConfig = {
  stories: ['../src/**/*.mdx', '../src/**/*.stories.@(js|jsx|mjs|ts|tsx)'],
  addons: [
    '@storybook/addon-webpack5-compiler-swc',
    '@storybook/addon-essentials',
    '@storybook/addon-onboarding',
  ],
  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },
  webpackFinal: async (config) => {
    return {
      ...config,
      resolve: {
        ...config.resolve,
        alias: {
          ...(config.resolve?.alias || {}),
          '@': path.resolve(__dirname, '../src'),
        },
      },
    };
  },
};
