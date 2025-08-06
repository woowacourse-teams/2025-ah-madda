import type { HttpErrorResponse } from '@/api/fetcher';
import { ACCESS_TOKEN_KEY } from '@/shared/constants';
import { removeLocalStorage } from '@/shared/utils/localStorage';

export const tokenErrorHandler = (responseText: HttpErrorResponse) => {
  if (responseText.detail === '인증 토큰 정보가 존재하지 않거나 유효하지 않습니다.') {
    removeLocalStorage(ACCESS_TOKEN_KEY);
    alert('로그인이 만료되었습니다. 다시 로그인해 주세요.');
    window.location.href = '/';
  }
};
