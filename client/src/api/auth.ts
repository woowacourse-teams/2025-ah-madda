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
  console.log('GOOGLE_CLIENT_ID', GOOGLE_CLIENT_ID);
  console.log('GOOGLE_REDIRECT_URI', GOOGLE_REDIRECT_URI);
  console.log('params', params.toString());

  return `https://accounts.google.com/o/oauth2/auth?${params.toString()}`;
};

export const getAuthCodeFromUrl = (): string | null => {
  const urlParams = new URLSearchParams(window.location.search);
  console.log('urlParams.get', urlParams.get('code'));
  return urlParams.get('code');
};

export const exchangeCodeForToken = async (
  code: string
): Promise<{
  access_token: string;
  user: {
    id: string;
    email: string;
    name: string;
    picture: string;
  };
}> => {
  const response = await fetch(`${SERVER_API_URL}/auth/google`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify({ code }),
  });

  if (!response.ok) {
    throw new Error(`HTTP error! status: ${response.status}`);
  }

  return response.json();
};

export const logout = (): void => {
  localStorage.removeItem('access_token');
  localStorage.removeItem('user');
  window.location.href = '/';
};

export const saveAuthData = (token: string, user: any): void => {
  localStorage.setItem('access_token', token);
  localStorage.setItem('user', JSON.stringify(user));
};

export const getStoredToken = (): string | null => {
  return localStorage.getItem('access_token');
};

export const getStoredUser = (): any | null => {
  const user = localStorage.getItem('user');
  return user ? JSON.parse(user) : null;
};

export const isAuthenticated = (): boolean => {
  return !!getStoredToken();
};
