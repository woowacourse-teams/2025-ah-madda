import React from 'react';

import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { fetcher } from '../api/fetcher';
import { MyEventPage } from '../features/Event/My/pages/MyEventPage';

import { TestContainer } from './customRender';
import { mockHostEvents } from './mocks/event';

vi.mock('../api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

describe('MyEventPage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  describe('MyEventPage 렌더링', () => {
    test('MyEventPage가 정상적으로 렌더링된다', async () => {
      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organizations/1/events/owned')) {
          return Promise.resolve(mockHostEvents);
        }
        if (url.includes('organizations/1/events/participated')) {
          return Promise.resolve([]);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(
        <TestContainer
          initialRoute="/event/my"
          routes={[{ path: '/event/my', element: <MyEventPage /> }]}
        />
      );

      await waitFor(() => {
        expect(screen.getByText('진행 중인 이벤트')).toBeInTheDocument();
      });
    });

    test('주최 이벤트 목록이 표시된다', async () => {
      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organizations/1/events/owned')) {
          return Promise.resolve(mockHostEvents);
        }
        if (url.includes('organizations/1/events/participated')) {
          return Promise.resolve([]);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(
        <TestContainer
          initialRoute="/event/my"
          routes={[{ path: '/event/my', element: <MyEventPage /> }]}
        />
      );

      await waitFor(() => {
        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
        expect(screen.getByText('테스트 이벤트 설명')).toBeInTheDocument();
        expect(screen.getByText('홍길동')).toBeInTheDocument();
      });

      await waitFor(() => {
        expect(screen.getByText('두 번째 이벤트')).toBeInTheDocument();
        expect(screen.getByText('두 번째 이벤트 설명')).toBeInTheDocument();
        expect(screen.getByText('김철수')).toBeInTheDocument();
      });
    });

    test('참여 현황이 올바르게 표시된다', async () => {
      mockFetcher.get.mockImplementation((url: string) => {
        if (url.includes('organizations/1/events/owned')) {
          return Promise.resolve(mockHostEvents);
        }
        if (url.includes('organizations/1/events/participated')) {
          return Promise.resolve([]);
        }
        return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
      });

      render(
        <TestContainer
          initialRoute="/event/my"
          routes={[{ path: '/event/my', element: <MyEventPage /> }]}
        />
      );

      await waitFor(() => {
        expect(screen.getByText('5/20명')).toBeInTheDocument();
        expect(screen.getByText('10/30명')).toBeInTheDocument();
      });
    });
  });
});
