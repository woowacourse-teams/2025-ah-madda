import { Tabs } from '@/shared/components/Tabs';

import { EventContainer } from '../containers/EventContainer';

import { CurrentEventList } from './CurrentEventList';
import { PastEventList } from './PastEventList';

type OverviewTabsProps = {
  organizationId: number;
};

export const OverviewTabs = ({ organizationId }: OverviewTabsProps) => {
  return (
    <EventContainer>
      <Tabs defaultValue="current">
        <Tabs.List>
          <Tabs.Trigger value="current">진행중인 이벤트</Tabs.Trigger>
          <Tabs.Trigger value="past">마감된 이벤트</Tabs.Trigger>
        </Tabs.List>

        <Tabs.Content value="current">
          <CurrentEventList organizationId={organizationId} />
        </Tabs.Content>

        <Tabs.Content value="past">
          <PastEventList organizationId={organizationId} />
        </Tabs.Content>
      </Tabs>
    </EventContainer>
  );
};
