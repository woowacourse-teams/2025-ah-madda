import ky, { Options, ResponsePromise } from 'ky';

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
        const token = localStorage.getItem('access_token');
        console.log(`token: ${token}`);
        if (token) {
          request.headers.set('Authorization', `Bearer ${token}`);
        }
      },
    ],
    afterResponse: [() => {}],
  },

  ...defaultOption,
});

export async function parseResponse<T>(response: ResponsePromise) {
  return await response.json<T>();
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
