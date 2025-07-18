import { useEffect } from 'react';

export const useFocusTrap = (
  containerRef: React.RefObject<HTMLElement | null>,
  onEscape?: () => void
) => {
  useEffect(() => {
    const container = containerRef.current;
    if (!container) return;

    const focusableElements = container.querySelectorAll<HTMLElement>(
      'button, a[href], input, textarea, select, [tabindex]:not([tabindex="-1"])'
    );
    const first = focusableElements[0];
    const last = focusableElements[focusableElements.length - 1];

    const isInIframe = window.self !== window.top;
    const originalOverflow = document.body.style.overflow;
    if (!isInIframe) {
      document.body.style.overflow = 'hidden';
    }

    first?.focus();

    const handleKeyDown = (e: KeyboardEvent) => {
      if (e.key === 'Escape') {
        e.preventDefault();
        onEscape?.();
      }

      if (e.key === 'Tab' && first && last) {
        if (e.shiftKey && document.activeElement === first) {
          e.preventDefault();
          last.focus();
        } else if (!e.shiftKey && document.activeElement === last) {
          e.preventDefault();
          first.focus();
        }
      }
    };

    container.addEventListener('keydown', handleKeyDown);
    return () => {
      container.removeEventListener('keydown', handleKeyDown);
      document.body.style.overflow = originalOverflow;
    };
  }, [containerRef, onEscape]);
};
