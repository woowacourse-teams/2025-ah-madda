import { useEffect, RefObject } from 'react';

type UseClickOutsideProps = {
  ref: RefObject<Element | null>;
  isOpen: boolean;
  onClose: () => void;
};

export const useClickOutside = ({ ref, isOpen, onClose }: UseClickOutsideProps) => {
  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (ref.current && !ref.current.contains(event.target as Node)) {
        onClose();
      }
    };

    if (isOpen) {
      document.addEventListener('mousedown', handleClickOutside);
    }

    return () => {
      document.removeEventListener('mousedown', handleClickOutside);
    };
  }, [isOpen, onClose, ref]);
};
