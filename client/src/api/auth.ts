// Google OAuth 설정
const GOOGLE_CLIENT_ID = process.env.REACT_APP_GOOGLE_CLIENT_ID || '';
const GOOGLE_REDIRECT_URI =
  process.env.REACT_APP_GOOGLE_REDIRECT_URI || 'http://localhost:3000/auth/callback';
const SERVER_API_URL = process.env.REACT_APP_SERVER_URL || 'http://localhost:8000';

export const getGoogleAuthUrl = (): string => {
  const params = new URLSearchParams({
    client_id: GOOGLE_CLIENT_ID,
    redirect_uri: GOOGLE_REDIRECT_URI,
    response_type: 'code',
    scope: 'openid email profile',
    access_type: 'offline',
    prompt: 'consent',
  });

  return `https://accounts.google.com/o/oauth2/auth?${params.toString()}`;
};

export const getAuthCodeFromUrl = (): string | null => {
  const urlParams = new URLSearchParams(window.location.search);
  return urlParams.get('code');
};

export const exchangeCodeForToken = async (
  code: string
): Promise<{
  accessToken: string;
}> => {
  const url = `${SERVER_API_URL}/members/login`;

  const response = await fetch(url, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ code }),
  });

  if (!response.ok) {
    const errorText = await response.text();

    throw new Error(`HTTP error! status: ${response.status}, message: ${errorText}`);
  }

  return response.json();
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
