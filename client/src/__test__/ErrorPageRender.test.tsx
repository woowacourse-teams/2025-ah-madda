import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect } from 'vitest';

import { ErrorPage } from '@/features/Error/pages/ErrorPage';

import { RouterWithQueryClient } from './customRender';

describe('잘못된 URL 접근 시 Error 페이지 렌더링', () => {
  test('잘못된 경로로 접근 시 Error 페이지가 렌더링된다', async () => {
    render(
      <RouterWithQueryClient
        initialRoute="/asdfasdf"
        routes={[{ path: '*', element: <ErrorPage /> }]}
      />
    );

    await waitFor(() => {
      expect(screen.getByText('문제가 발생했습니다')).toBeInTheDocument();
    });
  });
  //E.TODO 에러바운더리 설정 후 fallback UI에 대한 테스트 추가
});
