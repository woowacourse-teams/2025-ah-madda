import { lazy, Suspense } from 'react';

import { createBrowserRouter } from 'react-router-dom';

import { App } from '@/App';
import { EventDetailPage } from '@/features/Event/Detail/pages/EventDetailPage';
import { MyEventPage } from '@/features/Event/My/pages/MyEventPage';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';
import { HomePage } from '@/features/Home/page/HomePage';
import { OrganizationOverviewPage } from '@/features/Organization/Overview/pages/OrganizationOverviewPage';
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
          path: '/profile',
          Component: ProtectRoute,
          children: [
            {
              index: true,
              element: withSuspense(ProfilePage),
            },
          ],
        },
        {
          path: '/my',
          Component: ProtectRoute,
          children: [
            {
              index: true,
              Component: MyEventPage,
            },
          ],
        },
        {
          path: '/auth',
          Component: AuthCallback,
        },
        {
          path: '/:organizationId/event',
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
              path: ':eventId/edit',
              element: withSuspense(NewEventPage),
            },
            {
              path: ':eventId',
              Component: EventDetailPage,
            },
            {
              path: ':eventId/manage',
              Component: ProtectRoute,
              children: [
                {
                  index: true,
                  element: withSuspense(EventManagePage),
                },
              ],
            },
            {
              path: ':eventId/invite',
              Component: InviteRedirect,
            },
          ],
        },
        {
          path: '/organization',
          children: [
            {
              index: true,
              Component: OrganizationOverviewPage,
            },
            {
              path: 'new',
              Component: ProtectRoute,
              children: [
                {
                  index: true,
                  element: withSuspense(NewOrganizationPage),
                },
              ],
            },
            {
              path: ':organizationId/edit',
              Component: ProtectRoute,
              children: [
                {
                  index: true,
                  element: withSuspense(NewOrganizationPage),
                },
              ],
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
