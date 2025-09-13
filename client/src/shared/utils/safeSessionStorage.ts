export const safeSessionStorage = {
  get<T>(key: string): T | null {
    try {
      const storedString = sessionStorage.getItem(key);
      return storedString ? (JSON.parse(storedString) as T) : null;
    } catch {
      return null;
    }
  },
  set<T>(key: string, value: T) {
    try {
      sessionStorage.setItem(key, JSON.stringify(value));
      return true;
    } catch {
      return false;
    }
  },
  remove(key: string) {
    try {
      sessionStorage.removeItem(key);
      return true;
    } catch {
      return false;
    }
  },
};
