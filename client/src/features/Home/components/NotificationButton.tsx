import { Button } from '@/shared/components/Button';
import { useNotification } from '@/shared/notification/useNotification';

type NotificationButtonProps = {
  onClose: VoidFunction;
};
export const NotificationButton = ({ onClose }: NotificationButtonProps) => {
  const { permission, isLoading, handleNotificationClick } = useNotification();

  const handleNotifyClick = () => {
    handleNotificationClick().then((success) => {
      if (success) {
        onClose();
      }
    });
  };

  return (
    <Button size="full" onClick={handleNotifyClick} disabled={isLoading}>
      {isLoading ? '설정 중...' : permission === 'granted' ? '알림 허용됨' : '알림 받기'}
    </Button>
  );
};
