import { useNavigate } from 'react-router-dom';

import { Button } from '@/shared/components/Button';
import { Header } from '@/shared/components/Header';
import { IconButton } from '@/shared/components/IconButton';
import { PageLayout } from '@/shared/components/PageLayout';

import { EventTabs } from '../components/EventTabs';
import { MyEventContainer } from '../containers/MyEventContainer';

export const MyEventPage = () => {
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
      <MyEventContainer>
        <EventTabs />
      </MyEventContainer>
    </PageLayout>
  );
};
