import { defineConfig } from 'vitest/config';

export default defineConfig({
  test: {
    globals: true,
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
