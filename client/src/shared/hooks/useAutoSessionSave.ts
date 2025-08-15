import { useCallback, useEffect, useRef } from 'react';

import { safeSessionStorage } from '@/shared/utils/safeSessionStorage';

type UseAutoSessionSaveParams<T> = {
  key: string;
  data: T;
  delay?: number;
};

export function useAutoSessionSave<T>({ key, data, delay = 800 }: UseAutoSessionSaveParams<T>) {
  const timer = useRef<ReturnType<typeof setTimeout> | null>(null);
  const lastSavedRef = useRef<string>('');

  const payloadStr = JSON.stringify(data);

  const saveNow = useCallback(() => {
    if (lastSavedRef.current === payloadStr) return;
    safeSessionStorage.set(key, data);
    lastSavedRef.current = payloadStr;
  }, [key, data, payloadStr]);

  useEffect(() => {
    if (timer.current) clearTimeout(timer.current);
    timer.current = setTimeout(saveNow, delay);
    return () => {
      if (timer.current) clearTimeout(timer.current);
    };
  }, [saveNow, delay]);

  const restore = (): T | null => {
    const raw = safeSessionStorage.get<unknown>(key);
    const value = typeof raw === 'object' && raw !== null && 'data' in raw ? raw.data : raw;

    return value as T;
  };

  const clear = () => {
    safeSessionStorage.remove(key);
    lastSavedRef.current = '';
  };

  return { restore, clear };
}
