import { render, screen } from '@testing-library/react';

import { fetcher } from '@/api/fetcher';
import { OverviewPage } from '@/features/Event/Overview/pages/OverviewPage';

import { RouterWithQueryClient } from './customRender';
import { mockHostEvents } from './mocks/event';
import { mockOrganization } from './mocks/organization';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
  },
}));

const mockFetcher = vi.mocked(fetcher);

const renderOverviewPage = () => {
  render(
    <RouterWithQueryClient
      initialRoute="/event?organizationId=1"
      routes={[{ path: '/event', element: <OverviewPage /> }]}
    />
  );
};

describe('OverView 페이지 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  test('이벤트 전체 조회 시 조직 정보를 노출시킨다.', async () => {
    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('organizations/1/events')) {
        return Promise.resolve(mockHostEvents);
      }
      if (url.includes('organizations/1')) {
        return Promise.resolve(mockOrganization);
      }
      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });

    renderOverviewPage();

    expect(await screen.findByText('테스트 조직')).toBeInTheDocument();
    expect(await screen.findByText('테스트 조직 설명입니다.')).toBeInTheDocument();
  });

  test('이벤트 전체 조회 시 제목, 설명, 주최, 수용인원을 노출시킨다.', async () => {
    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('organizations/1/events')) {
        return Promise.resolve(mockHostEvents);
      }
      if (url.includes('organizations/1')) {
        return Promise.resolve(mockOrganization);
      }
      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });

    renderOverviewPage();

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
      if (url.includes('organizations/1')) {
        return Promise.resolve(mockOrganization);
      }
      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });

    renderOverviewPage();

    expect(await screen.findByText('등록된 이벤트가 없습니다.')).toBeInTheDocument();
  });
});
