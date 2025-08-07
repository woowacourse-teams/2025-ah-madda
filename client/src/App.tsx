import { Outlet } from 'react-router-dom';

import { usePageTrack } from './shared/hooks/usePageTrack';
import { useInitializeFCM } from './shared/notification/useInitializeFCM';

export const App = () => {
  usePageTrack();
  useInitializeFCM();

  return <Outlet />;
};
