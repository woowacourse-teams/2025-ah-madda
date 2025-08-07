import type { HttpErrorResponse } from '@/api/fetcher';
import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { removeLocalStorage } from '@/shared/utils/localStorage';

export const tokenErrorHandler = (responseText: HttpErrorResponse) => {
  if (responseText.detail === '만료기한이 지난 토큰입니다.') {
    removeLocalStorage(ACCESS_TOKEN_KEY);
    alert('로그인이 만료되었습니다. 다시 로그인해 주세요.');
    window.location.href = '/';
  }
};
