import { initializeApp } from 'firebase/app';
import { getMessaging, getToken, onMessage } from 'firebase/messaging';

const firebaseConfig = {
  apiKey: 'AIzaSyA1LwhH1JhJBBOlIC3F9RreoGIjo5RgV7Q',
  authDomain: 'ah-madda-d3207.firebaseapp.com',
  projectId: 'ah-madda-d3207',
  storageBucket: 'ah-madda-d3207.firebasestorage.app',
  messagingSenderId: '625252118355',
  appId: '1:625252118355:web:e15272dff0804f33f57ba6',
  measurementId: 'G-FTYX0MC1ZP',
};

const app = initializeApp(firebaseConfig);
export const messaging = getMessaging(app);

export const requestFCMPermission = async () => {
  try {
    const permission = await Notification.requestPermission();
    if (permission === 'granted') {
      return await getToken(messaging, {
        vapidKey: process.env.FCM_VAPID_KEY,
      });
    }

    if (permission === 'denied') {
      alert('알림 권한이 거부되었습니다.');
      return null;
    }

    if (permission === 'default') {
      // S.TODO : 권한 선택으로 유도할지
      alert('권한 선택을 하지 않았습니다.');
      return null;
    }
  } catch (error) {
    // TODO : FCM 토큰 획득 실패 시 처리 로직 추가
    console.error('FCM 토큰 획득 실패:', error);
    return null;
  }
};

export const setupForegroundMessage = () => {
  onMessage(messaging, (payload) => {
    if (Notification.permission === 'granted' && payload.notification) {
      const notification = new Notification(payload.notification?.title || '새 알림', {
        body: payload.notification?.body || '',
        icon: '/icon-192x192.png',
        data: payload.data,
      });

      notification.onclick = (event) => {
        const data = (event.target as Notification).data;
        const url = data?.redirectUrl;

        if (url) {
          window.open(url, '_blank');
        }
        notification.close();
      };
    }
  });
};
