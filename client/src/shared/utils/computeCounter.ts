export function computeCounter(value?: unknown, defaultValue?: unknown, maxLength?: number) {
  const hasMax = typeof maxLength === 'number' && maxLength > 0;

  const raw = value ?? defaultValue ?? '';
  const len =
    typeof raw === 'string' ? raw.length : typeof raw === 'number' ? String(raw).length : 0;

  const displayLength = hasMax ? Math.min(len, maxLength!) : len;

  return { hasMax, displayLength };
}
