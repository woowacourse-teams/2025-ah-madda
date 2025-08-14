import { Outlet } from 'react-router-dom';

import { ToastProvider } from './shared/components/Toast/ToastContext';
import { usePageTrack } from './shared/hooks/usePageTrack';
import { useInitializeFCM } from './shared/notification/useInitializeFCM';

export const App = () => {
  usePageTrack();
  useInitializeFCM();

  return (
    <ToastProvider>
      <Outlet />
    </ToastProvider>
  );
};
