import { useState, useEffect } from 'react';

import * as Sentry from '@sentry/react';
import { getToken } from 'firebase/messaging';

import { messaging } from './firebase';
import { registerFCMToken } from './useInitializeFCM';

export const useNotification = () => {
  const [permission, setPermission] = useState<NotificationPermission>('default');
  const [isLoading, setIsLoading] = useState(false);

  const handleNotificationClick = () => {
    return new Promise<boolean>((resolve) => {
      if (permission === 'granted') {
        alert('이미 알림을 받고 있습니다.');
        resolve(true);
        return;
      }

      setIsLoading(true);

      Notification.requestPermission()
        .then(async (permissionResult) => {
          setPermission(permissionResult);

          if (permissionResult === 'granted') {
            try {
              await navigator.serviceWorker.register('/firebase-messaging-sw.js');

              const token = await getToken(messaging, {
                vapidKey: process.env.FCM_VAPID_KEY,
              });

              if (token) {
                await registerFCMToken(token);
                alert('알림 설정이 완료되었습니다.');
                resolve(true);
              }
            } catch (error) {
              Sentry.captureException(error);
              alert('알림 설정에 실패했습니다.');
              resolve(false);
            }
          }
        })
        .catch(() => {
          resolve(false);
        })
        .finally(() => {
          setIsLoading(false);
        });
    });
  };

  useEffect(() => {
    if ('Notification' in window) {
      setPermission(Notification.permission);
    }
  }, []);

  return { permission, isLoading, handleNotificationClick };
};
