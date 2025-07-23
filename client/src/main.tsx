import ReactDOM from 'react-dom/client';
import { RouterProvider } from 'react-router-dom';

import './reset.css';

import { ClientQueryProvider } from './api/ClientQueryProvider';
import { router } from './router/route';

ReactDOM.createRoot(document.getElementById('root')!).render(
  // <React.StrictMode>
  <ClientQueryProvider>
    <RouterProvider router={router} />
  </ClientQueryProvider>
  // </React.StrictMode>
);
