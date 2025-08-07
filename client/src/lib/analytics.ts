import * as Sentry from '@sentry/react';

export const analytics = {
  trackApiCall: (endpoint: string, method: string, status?: number, duration?: number) => {
    Sentry.addBreadcrumb({
      category: 'api',
      message: `${method} ${endpoint}`,
      data: {
        endpoint,
        method,
        status,
        duration: duration ? `${duration.toFixed(2)}ms` : undefined,
      },
      level: status && status >= 400 ? 'error' : 'info',
    });
  },
};
