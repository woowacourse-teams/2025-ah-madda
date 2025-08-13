import { act, render, screen, waitFor } from '@testing-library/react';
import { wait } from '@testing-library/user-event/dist/cjs/utils/index.js';
import { Mocked } from 'vitest';

import { fetcher } from '@/api/fetcher';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';

import { RouterWithQueryClient } from './customRender';
import { mockHostEvents } from './mocks/event';
import { mockOrganization } from './mocks/organization';

vi.mock('../api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

const renderOverViewPage = () => {
  render(
    <RouterWithQueryClient
      initialRoute="/event"
      routes={[{ path: '/event', element: <OverviewPage /> }]}
    />
  );
};

describe('OverView 페이지 테스트', async () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('이벤트 전체 조회 시 조직 정보를 노출시킨다.', async () => {
    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('organizations/woowacourse')) {
        return Promise.resolve(mockOrganization);
      }
      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });

    renderOverViewPage();

    expect(await screen.findByText('테스트 조직')).toBeInTheDocument();
    expect(await screen.findByText('테스트 조직 설명입니다.')).toBeInTheDocument();
  });

  test('이벤트 전체 조회 시 제목, 설명, 주최, 수용인원을 노출시킨다.', async () => {
    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('organizations/1/events')) {
        return Promise.resolve(mockHostEvents);
      }
      if (url.includes('organizations/woowacourse')) {
        return Promise.resolve(mockOrganization);
      }
      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });

    renderOverViewPage();

    expect(await screen.findByText('테스트 이벤트')).toBeInTheDocument();
    expect(await screen.findByText('테스트 이벤트 설명')).toBeInTheDocument();

    expect(await screen.findByText('홍길동 주최')).toBeInTheDocument();
    expect(await screen.findByText('5 / 20')).toBeInTheDocument();
  });

  test('이벤트 전체 조회 시 이벤트가 없을 경우 "등록된 이벤트가 없습니다."를 노출시킨다.', async () => {
    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('organizations/1/events')) {
        return Promise.resolve([]);
      }

      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });

    renderOverViewPage();

    expect(await screen.findByText('등록된 이벤트가 없습니다.')).toBeInTheDocument();
  });
});
