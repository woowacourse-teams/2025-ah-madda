import { createContext, useContext, useState, useCallback, useMemo } from 'react';

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
  id: number;
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
    id: 0,
  });

  const closeToast = useCallback(() => {
    setToastState((prev) => ({ ...prev, isVisible: false }));
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
      setToastState((prev) => {
        const nextId = prev.id + 1;

        window.setTimeout(() => {
          setToastState((cur) => (cur.id === nextId ? { ...cur, isVisible: false } : cur));
        }, duration);

        return {
          message,
          variant,
          duration,
          isVisible: true,
          id: nextId,
        };
      });
    },
    []
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
          key={toastState.id}
          message={toastState.message}
          variant={toastState.variant}
          duration={toastState.duration}
          onClose={closeToast}
        />
      )}
    </ToastContext.Provider>
  );
};
