import ky, { Options, ResponsePromise, HTTPError } from 'ky';

import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { getLocalStorage } from '@/shared/utils/localStorage';

import { analytics } from '../lib/analytics';
import { Sentry } from '../lib/sentry';

const defaultOption: Options = {
  retry: 0,
  timeout: 30_000,
};
const API_BASE_URL = process.env.API_BASE_URL;

export const instance = ky.create({
  prefixUrl: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
  hooks: {
    beforeRequest: [
      (request) => {
        const token = getLocalStorage(ACCESS_TOKEN_KEY);
        if (token) {
          request.headers.set('Authorization', `Bearer ${token}`);
        }
        request.headers.set('X-Request-Start-Time', Date.now().toString());
      },
    ],
    afterResponse: [
      (request, options, response) => {
        const startTime = Number(request.headers.get('X-Request-Start-Time')) || Date.now();
        const duration = Date.now() - startTime;
        analytics.trackApiCall(request.url, request.method, response.status, duration);

        if (response.status >= 400) {
          Sentry.captureException(
            new Error(`API Error: ${response.status} ${response.statusText}`),
            {
              tags: {
                api_error: true,
                status_code: response.status.toString(),
              },
              extra: {
                url: request.url,
                method: request.method,
                status: response.status,
                statusText: response.statusText,
              },
            }
          );
        }
      },
    ],
  },

  ...defaultOption,
});

export async function parseResponse<T>(response: ResponsePromise) {
  try {
    return await response.json<T>();
  } catch (error) {
    if (error instanceof HTTPError) {
      const errorData = {
        status: error.response.status,
        statusText: error.response.statusText,
        url: error.response.url,
      };

      Sentry.captureException(error, {
        tags: {
          api_error: true,
          status_code: error.response.status.toString(),
        },
        extra: errorData,
      });
    } else {
      Sentry.captureException(error, {
        tags: {
          api_error: true,
          error_type: 'response_parse_error',
        },
      });
    }
    throw error;
  }
}

export const fetcher = {
  get: <T>(pathname: string, options?: Options) =>
    parseResponse<T>(instance.get(pathname, options)),
  post: <T>(pathname: string, options?: Options) =>
    parseResponse<T>(instance.post(pathname, options)),
  put: <T>(pathname: string, options?: Options) =>
    parseResponse<T>(instance.put(pathname, options)),
  delete: <T>(pathname: string, options?: Options) =>
    parseResponse<T>(instance.delete(pathname, options)),
  patch: <T>(pathname: string, options?: Options) =>
    parseResponse<T>(instance.patch(pathname, options)),
};
