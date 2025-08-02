/* eslint-disable no-undef */
importScripts('https://www.gstatic.com/firebasejs/12.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/12.0.0/firebase-messaging-compat.js');

const CACHE_NAME = 'ah-madda-cache-v1';
const urlsToCache = ['/manifest.json', '/icon-192x192.png', '/icon-512x512.png', '/offline.html'];

const firebaseConfig = {
  apiKey: 'AIzaSyA1LwhH1JhJBBOlIC3F9RreoGIjo5RgV7Q',
  authDomain: 'ah-madda-d3207.firebaseapp.com',
  projectId: 'ah-madda-d3207',
  storageBucket: 'ah-madda-d3207.firebasestorage.app',
  messagingSenderId: '625252118355',
  appId: '1:625252118355:web:e15272dff0804f33f57ba6',
  measurementId: 'G-FTYX0MC1ZP',
};

firebase.initializeApp(firebaseConfig);
const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
  const notificationTitle = payload.notification?.title || '새 알림';
  const notificationOptions = {
    body: payload.notification?.body || '내용 없음',
    icon: '/icon-512x512.png',
    data: payload.data,
  };

  self.registration.showNotification(notificationTitle, notificationOptions);
});

self.addEventListener('install', (event) => {
  event.waitUntil(
    caches
      .open(CACHE_NAME)
      .then((cache) => {
        return cache.addAll(urlsToCache);
      })
      .then(() => {
        return self.skipWaiting();
      })
  );
});

self.addEventListener('activate', (event) => {
  event.waitUntil(self.clients.claim());
});

self.addEventListener('fetch', (event) => {
  if (event.request.mode === 'navigate') {
    event.respondWith(
      fetch(event.request).catch(() => {
        return caches.match('/offline.html');
      })
    );
  } else {
    event.respondWith(
      caches.match(event.request).then((response) => {
        return response || fetch(event.request);
      })
    );
  }
});

self.addEventListener('notificationclick', (event) => {
  event.notification.close();
  const urlToOpen = event.notification.data?.url || '/';
  event.waitUntil(
    clients.matchAll({ type: 'window' }).then((clientList) => {
      for (const client of clientList) {
        if (client.url === urlToOpen && 'focus' in client) {
          return client.focus();
        }
      }

      if (clients.openWindow) {
        return clients.openWindow(urlToOpen);
      }
    })
  );
});
