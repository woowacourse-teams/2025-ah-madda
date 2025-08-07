import React from 'react';

import { render, screen, waitFor } from '@testing-library/react';
import { describe, expect, vi, beforeEach, Mocked } from 'vitest';

import { mockEventDetail } from '@/__test__/mocks/event';
import { fetcher } from '@/api/fetcher';
import { EventManagePage } from '@/features/Event/Manage/pages/EventManagePage';

import { RouterWithQueryClient } from './customRender';

vi.mock('@/api/fetcher', () => ({
  fetcher: {
    get: vi.fn(),
    post: vi.fn(),
  },
}));

const mockFetcher = fetcher as Mocked<typeof fetcher>;

describe('EventManagePage 테스트', () => {
  beforeEach(() => {
    vi.clearAllMocks();
    mockFetcher.get.mockImplementation((url: string) => {
      if (url.includes('organizations/events/123')) {
        return Promise.resolve(mockEventDetail);
      }
      return Promise.reject(new Error(`Unknown API endpoint: ${url}`));
    });
  });

  describe('EventInfoSection 렌더링', () => {
    test('EventInfoSection이 올바른 이벤트 정보를 표시한다', async () => {
      render(
        <RouterWithQueryClient
          initialRoute="/event/manage/123"
          routes={[{ path: '/event/manage/:eventId', element: <EventManagePage /> }]}
        />
      );

      await waitFor(() => {
        expect(screen.getByRole('tab', { name: '이벤트 정보' })).toBeInTheDocument();

        expect(screen.getByText('테스트 이벤트')).toBeInTheDocument();
        expect(screen.getByText('테스트 이벤트 설명')).toBeInTheDocument();
      });
    });

    test('주최자 정보가 올바르게 표시된다', async () => {
      render(
        <RouterWithQueryClient
          initialRoute="/event/manage/123"
          routes={[{ path: '/event/manage/:eventId', element: <EventManagePage /> }]}
        />
      );

      await waitFor(() => {
        expect(screen.getByText('주최자: 홍길동')).toBeInTheDocument();
      });
    });

    test('장소 정보가 표시된다', async () => {
      render(
        <RouterWithQueryClient
          initialRoute="/event/manage/123"
          routes={[{ path: '/event/manage/:eventId', element: <EventManagePage /> }]}
        />
      );

      await waitFor(() => {
        expect(screen.getByText('서울시 강남구')).toBeInTheDocument();
      });
    });
  });
});
