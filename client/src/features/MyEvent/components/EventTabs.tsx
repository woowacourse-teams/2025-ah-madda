import { Tabs } from '@/shared/components/Tabs';

import { UI_LABELS, STATUS_MESSAGES, TAB_VALUES } from '../constants';
import { useEvents } from '../hooks/useEvents';

import { EventSection } from './EventSection';
import { EventTabsList } from './EventTabsList';

export const EventTabs = () => {
  const { events } = useEvents();

  return (
    <Tabs defaultValue={TAB_VALUES.HOST}>
      <EventTabsList />

      <Tabs.Content value={TAB_VALUES.HOST} css={{ marginTop: '37.5px' }}>
        <EventSection
          events={events.hostEvents}
          title={UI_LABELS.ONGOING_HOST_EVENTS}
          emptyMessage={STATUS_MESSAGES.NO_HOST_EVENTS}
        />
      </Tabs.Content>

      <Tabs.Content value={TAB_VALUES.PARTICIPATE} css={{ marginTop: '37.5px' }}>
        <EventSection
          events={events.participateEvents}
          title={UI_LABELS.PARTICIPATING_EVENTS}
          emptyMessage={STATUS_MESSAGES.NO_PARTICIPATE_EVENTS}
        />
      </Tabs.Content>
    </Tabs>
  );
};
