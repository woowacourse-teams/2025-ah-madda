import { useEffect } from 'react';

import { requestFCMPermission, setupForegroundMessage } from './firebase';

export const useInitializeFCM = () => {
  useEffect(() => {
    const initializeFCM = async () => {
      try {
        if ('serviceWorker' in navigator) {
          await navigator.serviceWorker.register('/firebase-messaging-sw.js');
        }

        const token = await requestFCMPermission();
        if (token) {
          // TODO : 서버에 토큰 전송 로직 추가
          setupForegroundMessage();
        }
      } catch (error) {
        // TODO : FCM 초기화 실패 시 처리 로직 추가
        console.error('FCM 초기화 실패:', error);
      }
    };

    initializeFCM();
  }, []);
};
