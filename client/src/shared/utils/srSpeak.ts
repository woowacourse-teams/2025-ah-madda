let speakNonce = 0;

function ensureLiveRegion(kind: 'assertive' | 'polite') {
  const id = kind === 'assertive' ? 'a11y-live-assertive' : 'a11y-live-polite';
  let el = document.getElementById(id) as HTMLElement | null;
  if (!el) {
    el = document.createElement('div');
    el.id = id;
    el.setAttribute('aria-live', kind);
    el.setAttribute('aria-atomic', 'true');
    el.style.position = 'absolute';
    el.style.left = '-9999px';
    el.style.width = '1px';
    el.style.height = '1px';
    el.style.overflow = 'hidden';
    document.body.appendChild(el);
  }
  return el;
}
export function srSpeak(message: string, politeness: 'polite' | 'assertive' = 'assertive') {
  if (typeof document === 'undefined') return;
  const live = ensureLiveRegion(politeness);
  live.textContent = '';

  const nonce = `\u200B\u2060${(speakNonce++ % 100).toString()}`;
  const payload = `${message} ${nonce}`;
  requestAnimationFrame(() =>
    requestAnimationFrame(() => {
      live.textContent = payload;
    })
  );
}
