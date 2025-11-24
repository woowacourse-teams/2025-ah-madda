import { PageLayout } from '@/shared/components/PageLayout';

import { EventTabs } from '../components/EventTabs';
import { Info } from '../components/Info';
import { MyEventContainer } from '../containers/MyEventContainer';

export const MyEventPage = () => {
  return (
    <PageLayout>
      <MyEventContainer>
        <Info />
        <EventTabs />
      </MyEventContainer>
    </PageLayout>
  );
};
