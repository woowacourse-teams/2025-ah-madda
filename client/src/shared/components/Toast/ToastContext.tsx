import { createContext, useContext, useState, useRef, useCallback, useMemo } from 'react';

import { Toast, ToastVariant } from './Toast';

type ToastContextType = {
  success: (message: string, options?: { duration?: number }) => void;
  error: (message: string, options?: { duration?: number }) => void;
};

type ToastState = {
  message: string;
  variant: ToastVariant;
  duration: number;
  isVisible: boolean;
};

const ToastContext = createContext<ToastContextType | null>(null);

export const useToast = () => {
  const context = useContext(ToastContext);
  if (!context) throw new Error('useToast must be used within a ToastProvider');
  return context;
};

export const ToastProvider = ({ children }: { children: React.ReactNode }) => {
  const [toastState, setToastState] = useState<ToastState>({
    message: '',
    variant: 'success',
    duration: 3000,
    isVisible: false,
  });

  const timerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  const closeToast = useCallback(() => {
    setToastState((prev) => ({ ...prev, isVisible: false }));
    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }
  }, []);

  const openToast = useCallback(
    ({
      message,
      variant = 'success',
      duration = 3000,
    }: {
      message: string;
      variant?: ToastVariant;
      duration?: number;
    }) => {
      if (timerRef.current) {
        clearTimeout(timerRef.current);
        timerRef.current = null;
      }

      setToastState({ message, variant, duration, isVisible: true });

      timerRef.current = setTimeout(() => {
        closeToast();
      }, duration);
    },
    [closeToast]
  );

  const success = useCallback(
    (message: string, options?: { duration?: number }) => {
      openToast({ message, variant: 'success', duration: options?.duration });
    },
    [openToast]
  );

  const error = useCallback(
    (message: string, options?: { duration?: number }) => {
      openToast({ message, variant: 'error', duration: options?.duration });
    },
    [openToast]
  );

  const value = useMemo(() => ({ success, error }), [success, error]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      {toastState.isVisible && (
        <Toast
          message={toastState.message}
          variant={toastState.variant}
          duration={toastState.duration}
          onClose={closeToast}
        />
      )}
    </ToastContext.Provider>
  );
};
