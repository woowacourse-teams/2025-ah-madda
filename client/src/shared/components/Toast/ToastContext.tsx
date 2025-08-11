import { createContext, useContext, useState, useRef } from 'react';

import { Toast, ToastVariant } from './Toast';

type ToastContextType = {
  openToast: (options: { message: string; variant?: ToastVariant; duration?: number }) => void;
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

  const closeToast = () => {
    setToastState((prev) => ({ ...prev, isVisible: false }));
    if (timerRef.current) {
      clearTimeout(timerRef.current);
      timerRef.current = null;
    }
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
    closeToast();

    setToastState({ message, variant, duration, isVisible: true });

    timerRef.current = setTimeout(() => {
      closeToast();
    }, duration);
  };

  return (
    <ToastContext.Provider value={{ openToast }}>
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
