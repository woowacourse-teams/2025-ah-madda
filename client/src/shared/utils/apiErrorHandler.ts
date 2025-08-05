import * as Sentry from '@sentry/react';

import { HttpError } from '@/api/fetcher';

export const reportApiError = (error: HttpError) => {
  if (error instanceof HttpError) {
    Sentry.captureException(error, {
      tags: {
        api_error: true,
      },
    });
  } else {
    Sentry.captureException(error, {
      tags: {
        network_error: true,
      },
    });
  }
};
