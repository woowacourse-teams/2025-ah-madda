import path from 'path';
import { defineConfig } from 'vitest/config';

export default defineConfig({
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src'),
    },
  },
  test: {
    globals: true,
    environment: 'jsdom',
    setupFiles: ['./src/setupTest.ts'],
    include: ['src/__test__/*.test.ts?(x)'],
    exclude: [
      '**/node_modules/**',
      '**/dist/**',
      '**/.{git,cache,output,temp}/**',
      '**/*.config.*',
    ],
    pool: 'threads',
  },
});
