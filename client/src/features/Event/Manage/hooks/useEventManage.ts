import { useState, useEffect } from 'react';

import type { EventManageData } from '../types';

const mockEventManageData: EventManageData = {
  eventInfo: {
    id: '1',
    title: '솔라의 UI/UX 특강 @solar',
    description: 'UI/UX 디자인 연구하는 사람들을 위한 특강',
    organizer: '솔라',
    location: '잠실캠퍼스 굿샷 강의장',
    deadlineTime: '2025. 1. 14. 오후 3:00',
    startTime: '2025. 1. 14. 오후 5:00',
    endTime: '2025. 1. 14. 오후 10:00',
    currentParticipants: 42,
    maxParticipants: 50,
  },
  completedGuests: [
    { name: '김개발', status: '신청 완료' },
    { name: '이프론트', status: '신청 완료' },
    { name: '최플스택', status: '신청 완료' },
    { name: '정디자인', status: '신청 완료' },
  ],
  pendingGuests: [{ name: '박백엔드', status: '미신청' }],
};

const fetchEventManageData = async (eventId: string): Promise<EventManageData> => {
  await new Promise((resolve) => setTimeout(resolve, 1000));
  return mockEventManageData;
};

export const useEventManage = (eventId: string = '1') => {
  const [data, setData] = useState<EventManageData>({
    eventInfo: {
      id: '',
      title: '',
      description: '',
      organizer: '',
      location: '',
      deadlineTime: '',
      startTime: '',
      endTime: '',
      currentParticipants: 0,
      maxParticipants: 0,
    },
    completedGuests: [],
    pendingGuests: [],
  });
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const loadEventData = async () => {
      try {
        setLoading(true);
        setError(null);
        const eventData = await fetchEventManageData(eventId);
        setData(eventData);
      } catch (err) {
        setError(err instanceof Error ? err.message : '이벤트 데이터를 불러오는데 실패했습니다.');
      } finally {
        setLoading(false);
      }
    };

    loadEventData();
  }, [eventId]);

  return { data, loading, error };
};
