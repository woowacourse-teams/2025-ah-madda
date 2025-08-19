import { Button } from '@/shared/components/Button';
import { useNotification } from '@/shared/notification/useNotification';

type NotificationButtonProps = {
  onClose: VoidFunction;
};
export const NotificationButton = ({ onClose }: NotificationButtonProps) => {
  const { permission, handleNotificationClick } = useNotification();

  const handleNotifyClick = () => {
    handleNotificationClick();
    onClose();
  };
  return (
    <Button size="full" onClick={handleNotifyClick}>
      {permission === 'granted' ? '알림 허용' : '알림 받기'}
    </Button>
  );
};
