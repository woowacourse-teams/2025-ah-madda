import { createBrowserRouter } from 'react-router-dom';

import { App } from '@/App';
import { ErrorPage } from '@/features/Error/pages/ErrorPage';
import { EventDetailPage } from '@/features/Event/Detail/pages/EventDetailPage';
import { EventManagePage } from '@/features/Event/Manage/pages/EventManagePage';
import { MyEventPage } from '@/features/Event/My/pages/MyEventPage';
import { NewEventPage } from '@/features/Event/New/pages/NewEventPage';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';
import { HomePage } from '@/features/Home/page/HomePage';
import { InvitePage } from '@/features/Invite/page/InvitePage';
import { NewOrganizationPage } from '@/features/Organization/New/pages/NewOrganizationPage';
import { OrganizationSelectPage } from '@/features/Organization/Select/pages/SelectOrganizationPage';

import { AuthCallback } from './AuthCallback';
import { ProtectRoute } from './ProtectRoute';

export const router = createBrowserRouter(
  [
    {
      path: '/',
      Component: App,
      children: [
        {
          index: true,
          Component: HomePage,
        },
        {
          path: '/invite',
          Component: InvitePage,
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
              path: 'edit/:eventId',
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
        {
          path: '/organization',
          Component: ProtectRoute,
          children: [
            { path: 'new', Component: NewOrganizationPage },
            { path: 'edit/:organizationId', Component: NewOrganizationPage },
            { path: 'select', Component: OrganizationSelectPage },
          ],
        },
        {
          path: '*',
          Component: ErrorPage,
        },
      ],
    },
  ],
  {
    basename: '/',
  }
);
