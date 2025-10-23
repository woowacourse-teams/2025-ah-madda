let speakNonce = 0;

const getLiveRegion = (kind: 'assertive' | 'polite') => {
  const id = kind === 'assertive' ? 'a11y-live-assertive' : 'a11y-live-polite';
  return document.getElementById(id) as HTMLElement | null;
};

export function announce(message: string, politeness: 'polite' | 'assertive' = 'assertive') {
  if (typeof document === 'undefined') return;

  const live = getLiveRegion(politeness);
  if (!live) {
    if (process.env.NODE_ENV !== 'production') {
      console.warn(
        '[a11y] live region not found. Mount <A11y /> (ids: #a11y-live-assertive, #a11y-live-polite).'
      );
    }
    return;
  }

  live.textContent = '';
  const nonce = `\u200B\u2060${(speakNonce++ % 100).toString()}`;
  const payload = `${message} ${nonce}`;

  requestAnimationFrame(() =>
    requestAnimationFrame(() => {
      live.textContent = payload;
    })
  );
}
