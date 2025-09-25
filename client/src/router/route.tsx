import { lazy, Suspense } from 'react';

import { createBrowserRouter } from 'react-router-dom';

import { App } from '@/App';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';
import { HomePage } from '@/features/Home/page/HomePage';

import { AuthCallback } from './AuthCallback';
import { InviteRedirect } from './InviteRedirect';
import { ProtectRoute } from './ProtectRoute';

const ErrorPage = lazy(() =>
  import('@/features/Error/pages/ErrorPage').then((module) => ({
    default: module.ErrorPage,
  }))
);

const InvitePage = lazy(() =>
  import('@/features/Invite/page/InvitePage').then((module) => ({
    default: module.InvitePage,
  }))
);

const NewEventPage = lazy(() =>
  import(/* webpackChunkName: "event-pages" */ '@/features/Event/New/pages/NewEventPage').then(
    (module) => ({
      default: module.NewEventPage,
    })
  )
);

const EventDetailPage = lazy(() =>
  import(
    /* webpackChunkName: "event-pages" */ '@/features/Event/Detail/pages/EventDetailPage'
  ).then((module) => ({
    default: module.EventDetailPage,
  }))
);

const MyEventPage = lazy(() =>
  import(/* webpackChunkName: "event-pages" */ '@/features/Event/My/pages/MyEventPage').then(
    (module) => ({
      default: module.MyEventPage,
    })
  )
);

const EventManagePage = lazy(() =>
  import(
    /* webpackChunkName: "event-pages" */ '@/features/Event/Manage/pages/EventManagePage'
  ).then((module) => ({
    default: module.EventManagePage,
  }))
);

const OrganizationSelectPage = lazy(() =>
  import(
    /* webpackChunkName: "organization-pages" */ '@/features/Organization/Select/pages/SelectOrganizationPage'
  ).then((module) => ({
    default: module.OrganizationSelectPage,
  }))
);

const NewOrganizationPage = lazy(() =>
  import(
    /* webpackChunkName: "organization-pages" */ '@/features/Organization/New/pages/NewOrganizationPage'
  ).then((module) => ({
    default: module.NewOrganizationPage,
  }))
);

const ProfilePage = lazy(() =>
  import(/* webpackChunkName: "profile-page" */ '@/features/Profile/pages/ProfilePage').then(
    (module) => ({
      default: module.ProfilePage,
    })
  )
);

const withSuspense = (Component: React.ComponentType) => (
  <Suspense>
    <Component />
  </Suspense>
);

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
          element: withSuspense(InvitePage),
        },
        {
          path: '/auth',
          Component: AuthCallback,
        },
        {
          path: '/:organizationId/event',
          Component: ProtectRoute,
          children: [
            {
              index: true,
              Component: OverviewPage,
            },
            {
              path: 'new',
              element: withSuspense(NewEventPage),
            },
            {
              path: 'edit/:eventId',
              element: withSuspense(NewEventPage),
            },
            {
              path: 'my',
              element: withSuspense(MyEventPage),
            },
            {
              path: ':eventId',
              element: withSuspense(EventDetailPage),
            },
            {
              path: 'manage/:eventId',
              element: withSuspense(EventManagePage),
            },
            {
              path: ':eventId/invite',
              Component: InviteRedirect,
            },
          ],
        },
        {
          path: '/organization',
          Component: ProtectRoute,
          children: [
            {
              index: true,
              element: withSuspense(OrganizationSelectPage),
            },
            {
              path: 'new',
              element: withSuspense(NewOrganizationPage),
            },
            {
              path: 'edit/:organizationId',
              element: withSuspense(NewOrganizationPage),
            },
          ],
        },
        {
          path: '/:organizationId/profile',
          Component: ProtectRoute,
          children: [
            {
              index: true,
              element: withSuspense(ProfilePage),
            },
          ],
        },
        {
          path: '*',
          element: withSuspense(ErrorPage),
        },
      ],
    },
  ],
  {
    basename: '/',
  }
);
