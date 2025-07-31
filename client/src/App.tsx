import { Outlet } from 'react-router-dom';

import { usePageTrack } from './shared/hooks/usePageTrack';

export const App = () => {
  usePageTrack();

  return <Outlet />;
};
