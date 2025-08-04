import * as Sentry from '@sentry/react';

import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { getLocalStorage } from '@/shared/utils/localStorage';

import { analytics } from '../lib/analytics';

type HttpMethod = 'GET' | 'POST' | 'DELETE' | 'PATCH' | 'PUT';

type HttpErrorResponse = {
  type: string;
  title: string;
  status: number;
  detail: string;
  instance: string;
};

export class HttpError extends Error {
  constructor(
    public status: number,
    public data?: HttpErrorResponse
  ) {
    super(data?.detail || '요청 처리 중 오류가 발생했습니다.');
    this.name = 'HttpError';
  }
}

const request = async <T>(path: string, method: HttpMethod, body?: object): Promise<T> => {
  const startTime = Date.now();
  const url = process.env.API_BASE_URL + path;

  const config: RequestInit = {
    method,
    headers: {
      'Content-Type': 'application/json',
      Authorization: `Bearer ${getLocalStorage(ACCESS_TOKEN_KEY)}`,
    },
  };

  if (body) {
    config.body = JSON.stringify(body);
  }

  try {
    const response = await fetch(url, config);
    const duration = Date.now() - startTime;

    analytics.trackApiCall(url, method, response.status, duration);

    if (!response.ok) {
      Sentry.captureException(new Error(`API Error: ${response.status} ${response.statusText}`), {
        tags: {
          api_error: true,
          status_code: response.status.toString(),
        },
        extra: {
          url,
          method,
          status: response.status,
          statusText: response.statusText,
          duration,
        },
      });

      try {
        const errorData: HttpErrorResponse = await response.json();
        throw new HttpError(response.status, errorData);
      } catch {
        throw new HttpError(response.status);
      }
    }

    if (response.status === 204) return undefined as T;

    return response.json();
  } catch (error) {
    const duration = Date.now() - startTime;

    if (error instanceof HttpError) {
      Sentry.captureException(error, {
        tags: {
          api_error: true,
          error_category: 'http_error',
          status_code: error.status.toString(),
        },
        extra: {
          url,
          method,
          status: error.status,
          duration,
        },
      });
    } else {
      Sentry.captureException(error, {
        tags: {
          api_error: true,
          error_category: 'network_error',
        },
        extra: {
          url,
          method,
          duration,
        },
      });
    }

    throw error;
  }
};

export const fetcher = {
  get: <T>(path: string) => request<T>(path, 'GET'),
  post: <T>(path: string, body?: object) => request<T>(path, 'POST', body),
  patch: <T>(path: string, body?: object) => request<T>(path, 'PATCH', body),
  put: <T>(path: string, body?: object) => request<T>(path, 'PUT', body),
  delete: (path: string) => request<void>(path, 'DELETE'),
};
