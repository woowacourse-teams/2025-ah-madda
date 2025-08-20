import { useEffect } from 'react';

import * as Sentry from '@sentry/react';

import { isAuthenticated } from '@/api/auth';
import { fetcher } from '@/api/fetcher';

import { isIOS } from '../utils/device';

import { requestFCMPermission, setupForegroundMessage } from './firebase';

export const registerFCMToken = async (registrationToken: string) => {
  return await fetcher.post(`fcm-registration-tokens`, { registrationToken });
};

export const useInitializeFCM = () => {
  useEffect(() => {
    const initializeFCM = async () => {
      if (!('serviceWorker' in navigator) || isIOS()) return;

      try {
        await navigator.serviceWorker.register('/firebase-messaging-sw.js');
        const token = await requestFCMPermission();

        if (!token || !isAuthenticated()) return;

        await registerFCMToken(token);
        setupForegroundMessage();
      } catch (error) {
        // TODO : FCM 초기화 실패 시 처리 로직 추가
        Sentry.captureException(error);
      }
    };

    initializeFCM();
  }, []);
};
