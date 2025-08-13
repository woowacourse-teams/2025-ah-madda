export const safeSessionStorage = {
  get<T>(key: string): T | null {
    try {
      const raw = sessionStorage.getItem(key);
      return raw ? (JSON.parse(raw) as T) : null;
    } catch {
      return null;
    }
  },
  set<T>(key: string, value: T) {
    try {
      sessionStorage.setItem(key, JSON.stringify(value));
    } catch {
      return;
    }
  },
  remove(key: string) {
    try {
      sessionStorage.removeItem(key);
    } catch {
      return;
    }
  },
};
