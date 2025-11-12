import * as Sentry from '@sentry/react';
import { Outlet } from 'react-router-dom';

import { ErrorPage } from '@/features/Error/pages/ErrorPage';

import { ToastProvider } from './shared/components/Toast/ToastContext';
import { useChannelTalk } from './shared/hooks/useChannelTalk';
import { usePageTrack } from './shared/hooks/usePageTrack';
import { useInitializeFCM } from './shared/notification/useInitializeFCM';

export const App = () => {
  usePageTrack();
  useInitializeFCM();
  useChannelTalk();

  return (
    <Sentry.ErrorBoundary fallback={<ErrorPage />}>
      <ToastProvider>
        <Outlet />
      </ToastProvider>
    </Sentry.ErrorBoundary>
  );
};
