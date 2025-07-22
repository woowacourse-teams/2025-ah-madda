import { PageLayout } from '@/shared/components/PageLayout';

import { EventTabs } from '../components/EventTabs';
import { MyEventHeader } from '../components/MyEventHeader';
import { MyEventContainer } from '../containers/MyEventContainer';

export const MyEvent = () => {
  return (
    <PageLayout header={<MyEventHeader />}>
      <MyEventContainer>
        <EventTabs />
      </MyEventContainer>
    </PageLayout>
  );
};
