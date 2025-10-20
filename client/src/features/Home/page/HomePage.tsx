import { useEffect } from 'react';

import { isAuthenticated } from '@/api/auth';
import { PageLayout } from '@/shared/components/PageLayout';
import { isIOS, isPWA } from '@/shared/utils/device';

import { useModal } from '../../../shared/hooks/useModal';
import { AlarmModal } from '../components/AlarmModal';
import { Description } from '../components/Description';
import { Info } from '../components/Info';

export const HomePage = () => {
  const { isOpen, open, close } = useModal();

  const shouldShowModal =
    isAuthenticated() && isIOS() && isPWA() && Notification.permission === 'default';

  useEffect(() => {
    if (shouldShowModal) {
      open();
    }
  }, [open, shouldShowModal]);

  return (
    <>
      <PageLayout>
        <Info />
        <Description />
      </PageLayout>
      <AlarmModal isOpen={isOpen} onClose={close} />
    </>
  );
};
