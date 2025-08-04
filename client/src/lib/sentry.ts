import * as Sentry from '@sentry/react';

const SENTRY_DSN =
  process.env.SENTRY_DSN ||
  'https://ee98629c7681966d4c9aa6794c444f20@o4509767598997504.ingest.us.sentry.io/4509767646773248';

export const initSentry = () => {
  if (process.env.NODE_ENV !== 'production') {
    console.log('Sentry disabled in non-production environment');
    return;
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

    tracesSampleRate: process.env.NODE_ENV === 'production' ? 1.0 : 0.1,

    beforeSend(event) {
      if (event.exception) {
        const error = event.exception.values?.[0];
        if (error?.type === 'ReferenceError') {
          const errorMessage = error.value || '';

          const ignoredErrors = ['is not defined'];

          if (ignoredErrors.some((ignored) => errorMessage.includes(ignored))) {
            return null;
          }
        }
        if (error?.type === 'TypeError') {
          const errorMessage = error.value || '';
          const ignoredErrors = [
            'Cannot read properties of undefined',
            'Cannot read properties of null',
          ];
          if (ignoredErrors.some((ignored) => errorMessage.includes(ignored))) {
            return null;
          }
        }
      }

      return event;
    },

    initialScope: {
      tags: {
        app: 'ah-madda',
        version: process.env.SENTRY_RELEASE || '1.0.0',
      },
    },
  });
};

export { Sentry };
