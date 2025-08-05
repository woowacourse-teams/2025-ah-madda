import * as Sentry from '@sentry/react';

const SENTRY_DSN = process.env.SENTRY_DSN;

export const initSentry = () => {
  if (process.env.NODE_ENV !== 'production') {
    return;
  }

  Sentry.init({
    dsn: SENTRY_DSN,
    environment: process.env.NODE_ENV,
    sendDefaultPii: true,
    replaysOnErrorSampleRate: 0.1,
    integrations: [Sentry.replayIntegration({ maskAllText: true, blockAllMedia: true })],

    tracesSampleRate: process.env.NODE_ENV === 'production' ? 1.0 : 0.1,

    initialScope: {
      tags: {
        app: 'ah-madda',
        version: process.env.SENTRY_RELEASE || '1.0.0',
      },
    },
  });
};

export { Sentry };
