import React from 'react';

import { Navigate } from 'react-router-dom';

import { useGoogleAuth } from '../../hooks/useGoogleAuth';

type ProtectedRouteProps = {
  children: React.ReactNode;
};

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { isAuthenticated } = useGoogleAuth();

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
};
