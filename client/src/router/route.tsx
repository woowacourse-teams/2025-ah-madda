import { lazy, Suspense } from 'react';

import { createBrowserRouter } from 'react-router-dom';

import { App } from '@/App';
import { EventDetailPage } from '@/features/Event/Detail/pages/EventDetailPage';
import { MyEventPage } from '@/features/Event/My/pages/MyEventPage';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';
import { HomePage } from '@/features/Home/page/HomePage';
import { OrganizationSelectPage } from '@/features/Organization/Select/pages/SelectOrganizationPage';
import { Flex } from '@/shared/components/Flex';
import { Loading } from '@/shared/components/Loading';

import { AuthCallback } from './AuthCallback';
import { InviteRedirect } from './InviteRedirect';
import { ProtectRoute } from './ProtectRoute';

const ErrorPage = lazy(() =>
  import('@/features/Error/pages/ErrorPage').then((module) => ({
    default: module.ErrorPage,
  }))
);

const InvitePage = lazy(() =>
  import(/* webpackChunkName: "invite-pages" */ '@/features/Invite/page/InvitePage').then(
    (module) => ({
      default: module.InvitePage,
    })
  )
);

const NewEventPage = lazy(() =>
  import(/* webpackChunkName: "new-event-pages" */ '@/features/Event/New/pages/NewEventPage').then(
    (module) => ({
      default: module.NewEventPage,
    })
  )
);

const EventManagePage = lazy(() =>
  import(
    /* webpackChunkName: "event-manage-pages" */ '@/features/Event/Manage/pages/EventManagePage'
  ).then((module) => ({
    default: module.EventManagePage,
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
  <Suspense
    fallback={
      <Flex width="100%" height="100dvh" alignItems="center" justifyContent="center">
        <Loading />
      </Flex>
    }
  >
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
              Component: MyEventPage,
            },
            {
              path: ':eventId',
              Component: EventDetailPage,
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
              Component: OrganizationSelectPage,
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
