import { useState, useEffect } from 'react';

import * as Sentry from '@sentry/react';

import { isAuthenticated } from '@/api/auth';

import { requestFCMPermission } from './firebase';
import { registerFCMToken } from './useInitializeFCM';

export const useNotification = () => {
  const [permission, setPermission] = useState<NotificationPermission>('default');

  const handleNotificationClick = async () => {
    if (permission === 'granted') {
      alert('이미 알림을 받고 있습니다.');
      return;
    }

    try {
      const token = await requestFCMPermission();
      if (!token || !isAuthenticated()) return;

      await registerFCMToken(token);
      alert('알림 설정이 완료되었습니다.');
      setPermission('granted');
    } catch (error) {
      Sentry.captureException(error);
      alert('알림 설정에 실패했습니다.');
    }
  };

  useEffect(() => {
    const supported = 'Notification' in window;

    if (supported) {
      setPermission(Notification.permission);
    }
  }, []);

  return { permission, handleNotificationClick };
};
