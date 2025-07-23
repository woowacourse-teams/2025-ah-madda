import { css } from '@emotion/react';
import { useQuery } from '@tanstack/react-query';

import { myEventQueryOptions } from '@/api/queries/my';
import { Tabs } from '@/shared/components/Tabs';

import { Event } from '../../types/Event';
import { UI_LABELS, STATUS_MESSAGES, TAB_VALUES } from '../constants';

import { EventSection } from './EventSection';
import { EventTabsList } from './EventTabsList';

export const EventTabs = () => {
  // E.TODO: organizationMemberId를 실제로 가져오는 로직 필요
  const organizationMemberId = 1; // 임시 값

  const { data: hostEvents = [] } = useQuery({
    ...myEventQueryOptions.hostEvents(organizationMemberId),
    select: (data: Event[]) => data.map((event) => event),
  });

  const { data: participateEvents = [] } = useQuery({
    ...myEventQueryOptions.participateEvents(organizationMemberId),
    select: (data: Event[]) => data.map((event) => event),
  });

  return (
    <Tabs defaultValue={TAB_VALUES.HOST}>
      <EventTabsList />

      <Tabs.Content
        value={TAB_VALUES.HOST}
        css={css`
          margin-top: 37.5px;
        `}
      >
        <EventSection
          events={hostEvents}
          title={UI_LABELS.ONGOING_HOST_EVENTS}
          emptyMessage={STATUS_MESSAGES.NO_HOST_EVENTS}
        />
      </Tabs.Content>

      <Tabs.Content
        value={TAB_VALUES.PARTICIPATE}
        css={css`
          margin-top: 37.5px;
        `}
      >
        <EventSection
          events={participateEvents}
          title={UI_LABELS.PARTICIPATING_EVENTS}
          emptyMessage={STATUS_MESSAGES.NO_PARTICIPATE_EVENTS}
        />
      </Tabs.Content>
    </Tabs>
  );
};
