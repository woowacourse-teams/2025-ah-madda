import { fetcher } from './fetcher';

export type accessToken = {
  accessToken: string;
};

const GOOGLE_CLIENT_ID = process.env.REACT_APP_GOOGLE_CLIENT_ID || '';
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
  localStorage.removeItem('access_token');
  window.location.href = '/';
};

export const saveAuthData = (token: string): void => {
  localStorage.setItem('access_token', token);
};

export const getStoredToken = (): string | null => {
  return localStorage.getItem('access_token');
};

export const isAuthenticated = (): boolean => {
  return !!getStoredToken();
};
