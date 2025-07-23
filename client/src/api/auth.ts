import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { getLocalStorage, removeLocalStorage } from '@/shared/utils/localStorage';

import { fetcher } from './fetcher';

export type accessToken = {
  accessToken: string;
};

const GOOGLE_CLIENT_ID = process.env.GOOGLE_CLIENT_ID || '';
const GOOGLE_REDIRECT_URI = 'https://ahmadda.com';

export const getGoogleAuthUrl = (): string => {
  const params = new URLSearchParams({
    client_id: GOOGLE_CLIENT_ID,
    redirect_uri: GOOGLE_REDIRECT_URI,
    response_type: 'code',
    scope: 'openid email profile',
    access_type: 'offline',
    prompt: 'consent',
  });

  const authUrl = `https://accounts.google.com/o/oauth2/auth?${params.toString()}`;

  return authUrl;
};

export const getAuthCodeFromUrl = (): string | null => {
  const urlParams = new URLSearchParams(window.location.search);
  const code = urlParams.get('code');

  return code;
};

export const exchangeCodeForToken = async (code: string): Promise<accessToken> => {
  return fetcher.post<accessToken>('members/login', {
    json: { code },
  });
};

export const logout = (): void => {
  removeLocalStorage(ACCESS_TOKEN_KEY);
  window.location.href = '/';
};

export const isAuthenticated = (): boolean => {
  return !!getLocalStorage(ACCESS_TOKEN_KEY);
};
