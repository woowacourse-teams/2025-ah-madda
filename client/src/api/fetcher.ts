import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { getLocalStorage } from '@/shared/utils/localStorage';

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

  const response = await fetch(process.env.API_BASE_URL + path, config);

  if (!response.ok) {
    try {
      const responseText = await response.json();
      throw new HttpError(response.status, responseText);
    } catch (parseError) {
      if (parseError instanceof HttpError) {
        throw parseError;
      }

      throw new HttpError(response.status);
    }
  }

  if (response.status === 204) return undefined as T;

  const text = await response.text();
  if (!text) {
    return undefined as T;
  }

  return JSON.parse(text);
};

export const fetcher = {
  get: <T>(path: string) => request<T>(path, 'GET'),
  post: <T>(path: string, body?: object) => request<T>(path, 'POST', body),
  patch: <T>(path: string, body?: object) => request<T>(path, 'PATCH', body),
  put: <T>(path: string, body?: object) => request<T>(path, 'PUT', body),
  delete: (path: string) => request<void>(path, 'DELETE'),
};
