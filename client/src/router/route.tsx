import { createBrowserRouter } from 'react-router-dom';

import { EventDetailPage } from '@/features/Event/Detail/pages/EventDetailPage';
import { EventManagePage } from '@/features/Event/Manage/pages/EventManagePage';
import { MyEventPage } from '@/features/Event/My/pages/MyEventPage';
import { NewEventPage } from '@/features/Event/New/pages/NewEventPage';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';
import { HomePage } from '@/features/Home/page/HomePage';

import { AuthCallback } from './AuthCallback';
import { ProtectRoute } from './ProtectRoute';

export const router = createBrowserRouter(
  [
    {
      path: '/',
      Component: HomePage,
    },
    {
      path: '/auth',
      Component: AuthCallback,
    },
    {
      path: '/event',
      Component: ProtectRoute,
      children: [
        {
          index: true,
          Component: OverviewPage,
        },
        {
          path: 'new',
          Component: NewEventPage,
        },
        {
          path: 'my',
          Component: MyEventPage,
        },
        {
          path: ':eventId',
          Component: EventDetailPage,
        },
        {
          path: 'manage/:eventId',
          Component: EventManagePage,
        },
      ],
    },
  ],
  {
    basename: '/',
  }
);
