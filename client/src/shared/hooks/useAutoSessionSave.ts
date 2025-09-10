import { safeSessionStorage } from '@/shared/utils/safeSessionStorage';

type UseAutoSessionSaveParams<T> = {
  key: string;
  getData: () => T;
};

export function useAutoSessionSave<T>({ key, getData }: UseAutoSessionSaveParams<T>) {
  const save = () => {
    const snap = getData();
    safeSessionStorage.set(key, snap);
  };

  const restore = (): T | null => {
    return safeSessionStorage.get<T>(key);
  };

  const clear = () => {
    safeSessionStorage.remove(key);
  };

  return { save, restore, clear };
}
