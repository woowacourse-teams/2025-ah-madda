import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { reportApiError } from '@/shared/utils/apiErrorHandler';
import { getLocalStorage } from '@/shared/utils/localStorage';
import { tokenErrorHandler } from '@/shared/utils/tokenErrorHandler';

type HttpMethod = 'GET' | 'POST' | 'DELETE' | 'PATCH' | 'PUT';
type BuiltInit = Pick<RequestInit, 'headers' | 'body'>;

export type HttpErrorResponse = {
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
const serializeBody = (body?: object | FormData): BuiltInit => {
  if (!body) return {};
  if (typeof FormData !== 'undefined' && body instanceof FormData) {
    return { body };
  }
  return {
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(body),
  };
};

const withAuthHeader = (headers?: HeadersInit): HeadersInit => {
  return {
    Authorization: `Bearer ${getLocalStorage(ACCESS_TOKEN_KEY)}`,
    ...(headers ?? {}),
  };
};

const buildRequestInit = (method: HttpMethod, body?: object | FormData): RequestInit => {
  const { headers, body: serialized } = serializeBody(body);
  return {
    method,
    headers: withAuthHeader(headers),
    body: serialized,
  };
};

const request = async <T>(
  path: string,
  method: HttpMethod,
  body?: object | FormData
): Promise<T> => {
  const config = buildRequestInit(method, body);
  const response = await fetch(process.env.API_BASE_URL + path, config);

  if (!response.ok) {
    try {
      const responseText = await response.json();
      tokenErrorHandler(responseText);
      throw new HttpError(response.status, responseText);
    } catch (parseError) {
      if (parseError instanceof HttpError) {
        reportApiError(parseError);
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
  post: <T>(path: string, body?: object | FormData) => request<T>(path, 'POST', body),
  patch: <T>(path: string, body?: object | FormData) => request<T>(path, 'PATCH', body),
  put: <T>(path: string, body?: object) => request<T>(path, 'PUT', body),
  delete: (path: string) => request<void>(path, 'DELETE'),
};
