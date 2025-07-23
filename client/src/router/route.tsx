import { createBrowserRouter } from 'react-router-dom';

import { EventDetailPage } from '@/features/Event/Detail/pages/EventDetailPage';
import { EventManagePage } from '@/features/Event/Manage/pages/EventManagePage';
import { MyEventPage } from '@/features/Event/My/pages/MyEventPage';
import { NewEventPage } from '@/features/Event/New/pages/NewEventPage';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';

export const router = createBrowserRouter(
  [
    {
      path: '/event',
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
          path: 'detail/:eventId',
          Component: EventDetailPage,
        },
        {
          path: 'manage',
          Component: EventManagePage,
        },
      ],
    },
  ],
  {
    basename: '/',
  }
);
