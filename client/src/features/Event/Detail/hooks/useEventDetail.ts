import { useState, useEffect } from 'react';

import type { EventDetail } from '../types/index';

const mockEventDetail: EventDetail = {
  id: '1',
  title: '솔라의 UI/UX 활동 @solar',
  description: 'UX/UI에 관심있는 사람들을 위한 특강',
  author: '솔라',
  deadlineTime: '2025년 7월 14일 월요일 오후 03:00',
  startTime: '2025년 7월 14일 월요일 오후 05:00',
  endTime: '2025년 7월 14일 월요일 오후 10:00',
  location: '잠실캠퍼스 굿샷 강의장',
  currentParticipants: 42,
  maxParticipants: 50,
  preQuestions: [
    '이 이벤트에 참여하는 이유를 간단히 알려주세요',
    '이벤트를 통해 기대하는 점이 있다면 알려주세요',
  ],
};

const fetchEventDetail = async (id: string): Promise<EventDetail> => {
  await new Promise((resolve) => setTimeout(resolve, 500));
  return mockEventDetail; // 나중에 id에 따라 분기
};

export const useEventDetail = (id: string) => {
  const [event, setEvent] = useState<EventDetail | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadEventDetail = async () => {
      try {
        setLoading(true);
        setError(null);
        const data = await fetchEventDetail(id);
        setEvent(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : '이벤트 정보를 불러오지 못했습니다.');
      } finally {
        setLoading(false);
      }
    };

    loadEventDetail();
  }, [id]);

  return { event, loading, error };
};
