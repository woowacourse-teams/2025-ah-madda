import * as Sentry from '@sentry/react';

const SENTRY_DSN =
  process.env.SENTRY_DSN ||
  'https://ee98629c7681966d4c9aa6794c444f20@o4509767598997504.ingest.us.sentry.io/4509767646773248';

export const initSentry = () => {
  if (process.env.NODE_ENV === 'development') {
    console.log('Sentry initialized in development mode');
  }

  Sentry.init({
    dsn: SENTRY_DSN,
    environment: process.env.NODE_ENV,
    sendDefaultPii: true,
    integrations: [
      Sentry.replayIntegration(),
      Sentry.breadcrumbsIntegration({
        console: true,
        dom: true,
        fetch: true,
        history: true,
        xhr: true,
      }),
    ],

    tracesSampleRate: process.env.NODE_ENV === 'development' ? 1.0 : 0.1,

    replaysSessionSampleRate: 0.1,
    replaysOnErrorSampleRate: 1.0,

    initialScope: {
      tags: {
        app: 'ah-madda',
        version: process.env.SENTRY_RELEASE || '1.0.0',
      },
    },
  });
};

export { Sentry };
