import { useEffect, useRef } from 'react';

const OBSERVER_OPTIONS = {
  root: null,
  rootMargin: '20px',
  threshold: 0.1,
};

export const useInfiniteScroll = (callback: VoidFunction) => {
  const ref = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const observer = new IntersectionObserver((entries) => {
      entries.forEach((entry) => {
        if (entry.isIntersecting) {
          callback();
        }
      });
    }, OBSERVER_OPTIONS);

    const currentRef = ref.current;

    if (currentRef) {
      observer.observe(currentRef);
    }

    return () => {
      if (currentRef) {
        observer.unobserve(currentRef);
      }
    };
  }, [callback]);

  return { ref };
};
