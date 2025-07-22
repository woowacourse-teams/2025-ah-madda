import { createBrowserRouter } from 'react-router-dom';

import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';

export const router = createBrowserRouter([
  {
    path: '/',
    children: [
      {
        index: true,
        Component: OverviewPage,
      },
    ],
  },
]);
