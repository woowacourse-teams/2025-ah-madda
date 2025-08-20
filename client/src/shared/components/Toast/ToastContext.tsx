import { createContext, useContext, useState } from 'react';

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

  const closeToast = () => {
    setToastState((prev) => ({ ...prev, isVisible: false }));
  };

  const openToast = ({
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
  };

  const success = (message: string, options?: { duration?: number }) => {
    openToast({ message, variant: 'success', duration: options?.duration });
  };
  const error = (message: string, options?: { duration?: number }) => {
    openToast({ message, variant: 'error', duration: options?.duration });
  };

  return (
    <ToastContext.Provider value={{ success, error }}>
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
