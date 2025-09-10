import { safeSessionStorage } from '@/shared/utils/safeSessionStorage';

import { useToast } from '../components/Toast/ToastContext';

type UseAutoSessionSaveParams<T> = {
  key: string;
  getData: () => T;
};

export function useAutoSessionSave<T>({ key, getData }: UseAutoSessionSaveParams<T>) {
  const { success, error } = useToast();

  const save = () => {
    const draft = getData();
    const ok = safeSessionStorage.set(key, draft);
    if (ok) success('😀 임시 저장에 성공했어요!');
    else error('❌ 임시 저장에 실패했어요!');
  };

  const restore = (): T | null => {
    return safeSessionStorage.get<T>(key);
  };

  const clear = () => {
    safeSessionStorage.remove(key);
  };

  return { save, restore, clear };
}
