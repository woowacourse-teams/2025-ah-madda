import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Flex } from '@/shared/components/Flex';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventInfoSection } from '../components/EventInfoSection';
import { GuestManageSection } from '../components/GuestManageSection';
import { EventManageContainer } from '../containers/EventManageContainer';

export const EventManagePage = () => {
  const navigate = useNavigate();

  return (
    <PageLayout
      header={
        <Header
          left={<IconButton name="logo" size={55} onClick={() => navigate('/event')} />}
          right={
            <Button width="80px" size="sm" onClick={() => navigate('/event/my')}>
              내 이벤트
            </Button>
          }
        />
      }
    >
      <EventManageContainer>
        <Flex as="main" gap="40px" width="100%" dir="column">
          <EventInfoSection />
          <GuestManageSection />
        </Flex>
      </EventManageContainer>
    </PageLayout>
  );
};
