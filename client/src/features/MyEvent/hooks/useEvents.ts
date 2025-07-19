import { useState, useEffect } from 'react';

import type { EventsResponse } from '../types';

const mockEvents: EventsResponse = {
  hostEvents: [
    {
      id: '1',
      title: '솔라의 UI/UX 활동 @solar',
      description: 'UX/UI에 관심있는 사람들을 위한 특강',
      author: '솔라',
      deadlineTime: '오후 03:00',
      startTime: '오후 05:00',
      endTime: '오후 10:00',
      location: '잠실캠퍼스 굿샷 강의장',
      currentParticipants: 42,
      maxParticipants: 50,
      type: 'host',
    },
    {
      id: '2',
      title: '리액트 스터디 모임',
      description: '리액트 기초부터 심화까지 함께 공부해요',
      author: '김개발',
      deadlineTime: '오후 02:00',
      startTime: '오후 06:00',
      endTime: '오후 09:00',
      location: '강남역 스터디룸',
      currentParticipants: 8,
      maxParticipants: 12,
      type: 'host',
    },
    {
      id: '3',
      title: '리액트 스터디 모임',
      description: '리액트 기초부터 심화까지 함께 공부해요',
      author: '김개발',
      deadlineTime: '오후 02:00',
      startTime: '오후 06:00',
      endTime: '오후 09:00',
      location: '강남역 스터디룸',
      currentParticipants: 8,
      maxParticipants: 12,
      type: 'host',
    },
    {
      id: '4',
      title: '리액트 스터디 모임',
      description: '리액트 기초부터 심화까지 함께 공부해요',
      author: '김개발',
      deadlineTime: '오후 02:00',
      startTime: '오후 06:00',
      endTime: '오후 09:00',
      location: '강남역 스터디룸',
      currentParticipants: 8,
      maxParticipants: 12,
      type: 'host',
    },
    {
      id: '5',
      title: '리액트 스터디 모임',
      description: '리액트 기초부터 심화까지 함께 공부해요',
      author: '김개발',
      deadlineTime: '오후 02:00',
      startTime: '오후 06:00',
      endTime: '오후 09:00',
      location: '강남역 스터디룸',
      currentParticipants: 8,
      maxParticipants: 12,
      type: 'host',
    },
  ],
  participateEvents: [
    {
      id: '6',
      title: '디자인 시스템 워크샵',
      description: '실무에서 사용하는 디자인 시스템 구축 방법',
      author: '이디자이너',
      deadlineTime: '오후 01:00',
      startTime: '오후 07:00',
      endTime: '오후 11:00',
      location: '홍대 코워킹스페이스',
      currentParticipants: 15,
      maxParticipants: 20,
      type: 'participate',
    },
  ],
};

const fetchEvents = async (): Promise<EventsResponse> => {
  await new Promise((resolve) => setTimeout(resolve, 1000));
  return mockEvents;
};

export const useEvents = () => {
  const [events, setEvents] = useState<EventsResponse>({
    hostEvents: [],
    participateEvents: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadEvents = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await fetchEvents();
        setEvents(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : '이벤트를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    loadEvents();
  }, []);

  return { events, loading, error };
};
